# Security Checklist - Sistema de Nutrição

## ✅ Security Fixes Completed

### Critical (Completed)
- [x] Remove hardcoded credentials from application.yml
- [x] Fix CORS configuration (no wildcard with credentials)
- [x] Fix rate limiting bypass vulnerability
- [x] Remove information leakage in exception handlers
- [x] Disable SQL logging in production
- [x] Add N+1 query protection with JOIN FETCH
- [x] Add null safety checks in domain entities

### High Priority (Completed)
- [x] Create .env.example for configuration guidance
- [x] Document all required environment variables
- [x] Remove TRACE HTTP method from allowed methods
- [x] Restrict CORS headers to specific ones only

---

## 🔐 Security Best Practices to Follow

### 1. Environment Variables
**Status**: ✅ Implemented

All sensitive configuration must be in environment variables:
- JWT_SECRET (minimum 32 characters)
- MAIL_PASSWORD (app-specific password)
- DATABASE credentials
- API keys (if any)

**Never commit** `.env` file to git!

### 2. CORS Configuration
**Status**: ✅ Fixed

Current allowed origins:
- `http://localhost:3000` (development)
- `https://v0-sistema-nutricao-auth.vercel.app` (production)

**To update**: Modify `SecurityConfiguration.java:75-78`

### 3. Rate Limiting
**Status**: ✅ Fixed

Current limits:
- Login endpoint: 5 attempts per 15 minutes per IP
- Tracked via Redis

**To adjust**: Update `application.yml`:
```yaml
app:
  rate-limit:
    login:
      attempts: 5
      window: 900
```

### 4. Password Security
**Status**: ✅ Using BCrypt

- BCrypt password encoder configured
- Automatic salt generation
- 10 rounds (default)

**Best practice**: Enforce strong passwords in registration:
- Minimum 8 characters
- At least 1 uppercase, 1 lowercase, 1 number
- Special characters recommended

### 5. JWT Token Security
**Status**: ✅ Configured

Current settings:
- Access token: 24 hours
- Refresh token: 7 days
- Stateless session management

**Recommendations**:
- Shorter access token expiry (15-30 minutes)
- Implement token refresh endpoint
- Add token revocation (blacklist)

---

## 🚨 Security Issues to Address

### High Priority (TODO)

#### 1. Password Policy Enforcement
**Current**: Basic validation only
**Recommended**:
```java
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
private String password;
```

#### 2. Email Verification
**Current**: Email confirmation token exists
**Verify**:
- Token expiration working?
- Token single-use enforced?
- Secure token generation?

#### 3. Brute Force Protection
**Current**: Login rate limiting only
**Recommended**:
- Account lockout after failed attempts
- CAPTCHA after 3 failed attempts
- IP-based blocking

#### 4. SQL Injection Protection
**Current**: JPA with named parameters
**Status**: ✅ Protected (using JPA)
**Note**: Always use @Param and avoid string concatenation

#### 5. XSS Protection
**Current**: Spring Security default headers
**Verify**: Frontend sanitizes user input
**Add**: Content Security Policy headers

#### 6. CSRF Protection
**Current**: Disabled for stateless API
**Status**: ✅ OK (JWT-based auth)
**Note**: Keep disabled for REST API

---

## 🔍 Security Testing Checklist

### Authentication & Authorization
- [ ] Test login with wrong credentials (should fail)
- [ ] Test login with locked account (should fail)
- [ ] Test accessing protected endpoints without token (should return 401)
- [ ] Test accessing admin endpoints as regular user (should return 403)
- [ ] Test token expiration (should return 401)
- [ ] Test refresh token flow

### Rate Limiting
- [ ] Test 6+ login attempts from same IP (6th should be blocked)
- [ ] Test rate limit reset after window expires
- [ ] Test rate limit with different IPs

### Input Validation
- [ ] Test SQL injection in all input fields
- [ ] Test XSS payloads in text fields
- [ ] Test email validation
- [ ] Test password strength requirements
- [ ] Test file upload (if applicable)

### CORS
- [ ] Test requests from allowed origins (should succeed)
- [ ] Test requests from unauthorized origins (should fail)
- [ ] Test preflight OPTIONS requests

### Session Management
- [ ] Test concurrent sessions
- [ ] Test logout functionality
- [ ] Test token invalidation

### Data Protection
- [ ] Verify passwords are hashed (not plain text)
- [ ] Verify sensitive data is not logged
- [ ] Verify HTTPS enforced in production
- [ ] Verify error messages don't leak info

---

## 🛡️ Security Headers to Add

### Current (Spring Security defaults)
- X-Content-Type-Options: nosniff
- X-Frame-Options: DENY
- X-XSS-Protection: 1; mode=block

### Recommended to Add
Add to `SecurityConfiguration.java`:

```java
http.headers(headers -> headers
    .contentSecurityPolicy(csp -> csp
        .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'"))
    .referrerPolicy(referrer -> referrer
        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
    .permissionsPolicy(permissions -> permissions
        .policy("geolocation=(), microphone=(), camera=()"))
);
```

---

## 📊 Security Monitoring

### Logging (TODO)
- [ ] Log all authentication attempts (success/failure)
- [ ] Log all authorization failures
- [ ] Log all rate limit violations
- [ ] Log all suspicious activities
- [ ] **Never log sensitive data** (passwords, tokens, PII)

### Metrics to Track
- Failed login attempts per user/IP
- Token refresh rate
- API error rates
- Unusual access patterns
- Database query performance

### Alerts to Configure
- Multiple failed logins (>5 in 5 min)
- Admin account access
- Database connection failures
- High error rates (>5% of requests)
- Unusual traffic spikes

---

## 🔄 Regular Security Tasks

### Daily
- Monitor error logs for security issues
- Check failed authentication attempts

### Weekly
- Review access logs for anomalies
- Update security documentation
- Check for dependency vulnerabilities: `mvn dependency-check:check`

### Monthly
- Update dependencies: `mvn versions:display-dependency-updates`
- Review and rotate secrets if needed
- Security audit of new features
- Penetration testing (if applicable)

### Quarterly
- Full security audit
- Update security policies
- Review and update CORS origins
- Review rate limiting thresholds

---

## 🚀 Pre-Production Security Checklist

Before deploying to production:

### Configuration
- [ ] All environment variables set correctly
- [ ] JWT_SECRET is strong (32+ random characters)
- [ ] CORS origins configured for production domain
- [ ] Database credentials are secure
- [ ] Email service configured with app password
- [ ] Redis is secured (password protected)

### Code
- [ ] No hardcoded secrets in code
- [ ] No debug logging in production
- [ ] SQL logging disabled
- [ ] Error messages don't leak information
- [ ] All endpoints have proper authorization

### Infrastructure
- [ ] HTTPS enabled with valid certificate
- [ ] Database connections encrypted (SSL)
- [ ] Redis password protected
- [ ] Firewall rules configured
- [ ] Backup strategy in place
- [ ] Monitoring and alerting configured

### Testing
- [ ] Security tests pass
- [ ] Penetration testing completed
- [ ] Vulnerability scan completed
- [ ] Load testing completed
- [ ] OWASP Top 10 reviewed

### Documentation
- [ ] Security policies documented
- [ ] Incident response plan ready
- [ ] Backup/restore procedures tested
- [ ] Team trained on security practices

---

## 📚 Additional Resources

### Tools
- **OWASP ZAP**: Web application security scanner
- **Dependency Check**: `mvn dependency-check:check`
- **SonarQube**: Code quality and security
- **Snyk**: Dependency vulnerability scanning

### Documentation
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

### Security Standards
- Follow OWASP guidelines
- Implement least privilege principle
- Practice defense in depth
- Keep all dependencies updated
- Regular security training for team

---

## 📞 Incident Response

If a security breach is detected:

1. **Immediate Actions**
   - Isolate affected systems
   - Revoke compromised credentials
   - Enable additional logging
   - Document everything

2. **Investigation**
   - Determine scope of breach
   - Identify attack vector
   - Assess data exposure
   - Preserve evidence

3. **Remediation**
   - Patch vulnerabilities
   - Rotate all secrets
   - Update security measures
   - Notify affected users (if required)

4. **Post-Incident**
   - Root cause analysis
   - Update security policies
   - Implement preventive measures
   - Team training/awareness

---

**Last Updated**: 2025-10-01
**Next Review**: Monthly

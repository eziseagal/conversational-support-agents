# Single Sign-On (SSO) Setup Guide

## Supported Providers
We currently support SAML 2.0 and OpenID Connect (OIDC) for Enterprise customers. Supported Identity Providers (IdPs) include Okta, Microsoft Entra ID (formerly Azure AD), and Google Workspace.

## Configuration Steps for SAML
1. Navigate to Settings > Security > SSO Configuration in your dashboard.
2. Enter your IdP's Entity ID and Single Sign-On URL.
3. Upload your IdP's X.509 public certificate in `.pem` or `.crt` format.
4. Map the `email` attribute. This is the only required claim, but we also recommend mapping `firstName` and `lastName`.
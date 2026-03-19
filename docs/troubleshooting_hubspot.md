# HubSpot Integration Troubleshooting

## Connection Failures
If your integration with HubSpot keeps failing or disconnecting, check the following:
1. **OAuth Scopes**: Ensure your HubSpot Private App has the `crm.objects.contacts.read` and `crm.objects.companies.write` scopes enabled.
2. **Token Expiration**: HubSpot OAuth tokens expire every 30 minutes. Ensure your backend is properly using the refresh token to generate a new access token. If the refresh token is invalid, the user must re-authenticate.

## Data Sync Delays
If contacts are syncing but taking too long, verify that your webhook listener is responding with a 200 OK within 3 seconds. If HubSpot does not receive a 200 OK, it will retry the webhook, causing a backlog.
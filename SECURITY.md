# Security Policy

This project handles glass makers, cutters, grinders and finishers operating
workflows. Treat vulnerabilities as potentially high impact even when the
demo data is synthetic — this domain's failure modes include physical
worker-safety risk from furnace burn exposure, sharp-glass cut hazards and
grinding-dust/respiratory hazards.

## Do Not Disclose Publicly

Report privately before opening public issues for:

- credential exposure
- real glassworker, workshop or operator data exposure
- authorization bypass
- Glass Workshop Coordination Governor bypass
- audit-ledger tampering
- over-disclosure in reports or exports
- unsafe robot action dispatch
- any path that lets a proposal reach a glass-forming/cutting-execution
  decision, or a workshop-safety-officer-override decision

## Reporting

Use GitHub private vulnerability reporting when available for the repository.
If that is unavailable, contact the repository maintainers through the
cloud-itonami organization before publishing details.

Include:

- affected commit or version
- reproduction steps
- expected and actual behavior
- impact on glassworker/workshop data, policy enforcement or audit logging
- suggested fix, if known

## Production Guidance

- Store secrets outside Git.
- Keep real glassworker/workshop/operator data outside this repository.
- Run policy tests before deployment.
- Export and review audit logs regularly.
- Use least privilege for operators and service accounts.

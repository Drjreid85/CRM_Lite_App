# CRM_Lite_App

**CRM Lite** is a lightweight Java desktop CRM (JavaFX + SQLite) for quick contact and follow-up management.

## Features
- Search by **Name / Company / Industry**
- Store **email**, **phone**, **next follow-up**
- Log **Calls / Emails / Meetings**
- Row highlight: **orange = due soon**, **red = overdue**
- Local data file: `crm_lite.db`

## Requirements
- Java **17+** (tested with Java **24**)
- JavaFX SDK **24** (unzipped locally)

## Install (user-level “global” CLI)
```bash
chmod +x installer/install-user.sh
./installer/install-user.sh
# then launch from anywhere:
crm-lite

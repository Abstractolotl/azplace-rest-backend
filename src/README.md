# AzPlace REST-Backend

This repository is a part of **AzPlace**, which consists of three different parts. 
This repository contains the Spring REST-Service.

To use **AzPlace** you also need these services:

- [WebSocket-Server](https://github.com/Abstractolotl/azplace-websocket-backend)
- [Frontend](https://github.com/Abstractolotl/azplace-frontend)

## Requirements

Before running this service you need the following other services:
- Redis-Server
- MariaDB-/MySQL-Server
- Elasticsearch Instance

## Database

```mermaid
erDiagram
    Board }o..|| ColorPalette : needs
    
    UserCooldown }o..|| Board : on
    
    User ||..o{ UserCooldown : has
    User ||..o{ UserBan : has
    
    PixelOwner }o..|| Board : on
    PixelOwner }o..|| User : is
```

## Login Flow

```mermaid
sequenceDiagram
    User->>+REST: Request login (/auth/login)
    REST->>-User: Redirect to CAS
    User->>+CAS: Login
    CAS->>-User: Redirect to REST
    User->>+REST: Request verification
    REST->>+CAS: Validate Ticket
    CAS->>-REST: Respond User Data
    REST->>-User: Redirect to Frontend
```



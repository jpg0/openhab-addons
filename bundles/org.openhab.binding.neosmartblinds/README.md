# NeoSmartBlinds Binding

This binding allows you to control NeoSmart Blinds via their WiFi Smart Controller (Hub). 
It communicates with the hub over the local network using a simple HTTP API.

## Supported Things

### Hub (Bridge)
The Hub represents the NeoSmart Smart Controller (Bridge). 
It manages the connection to the hub and handles a command queue to ensure that requests are spaced out correctly.

- **Thing Type ID**: `neosmartblinds:hub`

### Blind (Thing)
The Blind represents an individual motorized blind or a group of blinds controlled by a single RF code.

- **Thing Type ID**: `neosmartblinds:blind`

## Discovery

Currently, discovery is not supported. Hubs and Blinds must be configured manually using their IP addresses and device codes.

## Thing Configuration

### Hub Configuration

| Name | Type | Description | Default | Required |
|------|------|-------------|---------|----------|
| host | text | IP address or hostname of the hub | N/A | yes |
| hubId | text | 24-character Hub ID found in the NeoSmart app | N/A | yes |
| port | integer | Port to use (typically 8838 for HTTP) | 8838 | yes |
| commandDelay | integer | Delay in ms between consecutive commands | 600 | no (advanced) |

### Blind Configuration

| Name | Type | Description | Default | Required |
|------|------|-------------|---------|----------|
| blindCode | text | The ID/Channel of the blind (e.g., 211.179-15) | N/A | yes |
| motorCode | text | Optional motor code (required for some hubs like C-BR300) | N/A | no |

## Channels

| Channel | Type | Description |
|---------|------|-------------|
| rollershutter | Rollershutter | Standard UP / DOWN / STOP control. |
| position | Dimmer | Direct position control (0-100%). |
| favourite1 | Switch | Trigger the first favorite position. |
| favourite2 | Switch | Trigger the second favorite position. |

## Full Example

### neosmart.things

```java
Bridge neosmartblinds:hub:myhub [ host="192.168.XXX.XXX", hubId="3900XXXXXXXXXXXXXXXXXXXX" ] {
    Thing blind kitchen [ blindCode="123.456-78" ]
    Thing blind bedroom [ blindCode="123.456-79", motorCode="01" ]
}
```

### neosmart.items

```java
Rollershutter Kitchen_Blinds "Kitchen Blinds" <rollershutter> { channel="neosmartblinds:blind:myhub:kitchen:rollershutter" }
Dimmer Kitchen_Blinds_Pos "Kitchen Blinds Position" { channel="neosmartblinds:blind:myhub:kitchen:position" }
Switch Kitchen_Blinds_Fav1 "Fav 1" { channel="neosmartblinds:blind:myhub:kitchen:favourite1" }
```

## Important Notes

### Command Rate-Limiting
The NeoSmart Hub has a simple RF transmitter that can only handle one command at a time. 
To prevent commands from being dropped, this binding implements a mandatory queue with a **600ms delay** by default. 
If you send multiple commands at once (e.g., via a rule), they will be processed sequentially.

### Stop Command
Some hub models use a proprietary `sp` code for stopping movement. 
This binding handles this translation automatically.

# Sync Sign Notice Mod

A Minecraft Fabric mod that synchronizes sign content with a database and displays news items.

**THIS IS A CLIENT ONLY MOD.**

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for your Minecraft version
2. Download the mod jar file
3. Place it in your `mods` folder

## Usage

1. Start Minecraft with Fabric Loader
2. The mod will automatically connect to the configured database
3. Signs will display synchronized news items

## Configuration

Edit the config file at `run/config/sync-sign-notice/config.json`:

```json
{
  "db_host": "example.com",       // Database host address
  "db_user": "ExampleUser",       // Database username
  "db_password": "ExamplePassword", // Database password
  "db_database": "example_db",    // Database name
  "db_port": 3306,               // Database port (default: 3306)
  "maxNewsCount": 5,             // Maximum number of news items to display
  "autoRefreshInterval": 300     // Auto-refresh interval in seconds (default: 300)
}
```

### Configuration Details

- **Database Connection**: 
  - Fill in your MySQL database credentials
  - Ensure the database is accessible from your Minecraft server

- **News Settings**:
  - `maxNewsCount`: Limits how many news items are displayed
  - `autoRefreshInterval`: How often to refresh news (in seconds)

## Sign Setup

To create a synchronized notice sign in your Minecraft world:

1. Place a sign (any wood type)
2. Edit the sign text as follows:
   - Line 1: `[Notice]` (exactly like this)
   - Line 2: News item number (from the newest item to the oldest, starting from 1, no more than `maxNewsCount`)
   - Line 3: (Leave blank or put custom text)
   - Line 4: `SyncSign` (exactly like this)

Example sign text:
```
[Notice]
1
Welcome!
SyncSign
```

The mod will:
- Automatically update the sign content when news changes
- Preserve any custom text on line 3
- Only update signs with the exact "[Notice]" and "SyncSign" markers

## Database Schema

The mod expects a table named `jellynews`(Just a cute name) with the following structure:

```sql
CREATE TABLE jellynews (
    created_at TIMESTAMP,  -- When the news was created
    type VARCHAR(255),     -- News category/tag
    name VARCHAR(255),     -- Author name
    content TEXT           -- News content
);
```

### Data Organization Guidelines:
1. Each news item should have:
   - `created_at`: Timestamp of creation (auto-filled if using CURRENT_TIMESTAMP)
   - `type`: Category/tag for filtering (e.g., "announcement", "event")
   - `name`: Author or source of the news
   - `content`: The actual news text to display on signs

2. The mod will:
   - Fetch the most recent news items first
   - Respect the `maxNewsCount` limit from config
   - Refresh according to `autoRefreshInterval`

## Features

- Automatically updates sign content from database
- Configurable refresh interval
- Supports MySQL databases

## Troubleshooting

- If signs don't update, check:
  - Database connection settings
  - Database server accessibility
  - Minecraft server logs for errors
  - Table structure matches expected schema

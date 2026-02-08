> [!CAUTION]
> ## Repository is archived as I'm leaving GitHub!
>
> GitHub wants me to "embrace AI or get out". So I'm leaving.
> 
> Continued development will happen on Codeberg: https://codeberg.org/Phoenix616/s3redirector

# S3Redirector
Small server which authenticates with an S3 object storage service, generates signed URLs, and redirects to them. Does not serve any files itself!

While this program includes some basic caching I strongly suggest running this behind a caching reverse proxy like nginx. (Especially if you want to use SSL! Which you should.)

Requires Java 17.

## Configuration

The configuration is either done via system properties, a config.properties or environment variables. They are read in that order.

| Property           | Config         | Environment        | Default     | Description                                 |
|--------------------|----------------|--------------------|-------------|---------------------------------------------|
| `s3r.port`         | `port`         | `S3R_PORT`         | `8053`      | The port to listen on                       |
| `s3r.host`         | `host`         | `S3R_HOST`         | `127.0.0.1` | The host to listen on                       |
| `s3r.redirectcode` | `redirectcode` | `S3R_REDIRECTCODE` | `302`       | The HTTP status code to use for redirects   |
| `s3r.accesskey`    | `accesskey`    | `S3R_ACCESSKEY`    |             | The access key to use for authentication    |
| `s3r.secretkey`    | `secretkey`    | `S3R_SECRETKEY`    |             | The secret key to use for authentication    |
| `s3r.region`       | `region`       | `S3R_REGION`       |             | The region of the S3 service                |
| `s3r.bucket`       | `bucket`       | `S3R_BUCKET`       |             | The bucket to use                           |
| `s3r.endpoint`     | `endpoint`     | `S3R_ENDPOINT`     |             | The endpoint to use for the S3 service      |
| `s3r.expiration`   | `expiration`   | `S3R_EXPIRATION`   | `3600`      | The expiration time for signed URLs in secs |
| `s3r.cachesize`    | `cachesize`    | `S3R_CACHESIZE`    | `10000`     | The size of the url cache                   |
| `s3r.debug`        | `debug`        | `S3R_DEBUG`        | `false`     | Whether to enable debug logging of requests |

## Downloads
Downloads are currently available on the Minebench.de CI server: https://ci.minebench.de/job/s3redirector/

## License
This program is licensed under the terms of the [AGPLv3](LICENSE).

```
 Copyright (C) 2023 Max Lee aka Phoenix616 (max@themoep.de)

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published
 by the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

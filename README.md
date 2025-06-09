# discogs  [![Clojure CI](https://github.com/iomonad/discogs/actions/workflows/clojure.yml/badge.svg?branch=master)](https://github.com/iomonad/discogs/actions/workflows/clojure.yml) [![Clojars Project](https://img.shields.io/clojars/v/io.trosa/discogs.svg)](https://clojars.org/io.trosa/discogs) [![cljdoc badge](https://cljdoc.org/badge/io.trosa/discogs)](https://cljdoc.org/d/io.trosa/discogs) <a href="https://github.com/iomonad/components"><img src=".github/banner.png" height="280" align="right"></a>

> [!NOTE]
> A fully-featured, data-oriented, [Discogs](https://discogs.com) API Client written in pure clojure.

## Usage

### Create a Client

There is differents way to create a Discogs client, here is a detailed guide to find what fit the best for your use-case:

#### 1. Create anonymous client
> [!NOTE]
> Requests are throttled by the server by source IP to **60** per minute for authenticated requests, and **25 per minute** for unauthenticated requests, with some exceptions.

```clojure
(require '[discogs.client :as dc])

(def client (dc/mk-client))
```


#### 2. Create client for application Key & Secrets

- **Go to Discogs Developers Portal:**  
  Visit [Discogs API Documentation](https://www.discogs.com/developers/) to create your developer account
- **Create an App:**  
  In your developer settings, create a new application. You will receive a **Consumer Key** and **Consumer Secret**.

```clojure
(require '[discogs.client :as dc])

(def client (dc/mk-client "ZrUgstGwOmYzWChuPXrH" "S3FEVERXljTmdQZ3ZoY0NocloK"))
```

#### 3. Create Using PAT

To generate a Personal Access Token (PAT) for the Discogs API, follow these steps:

1. **Log in to your Discogs Account:**  
   Go to the Discogs website and sign in with your username and password.
2. **Navigate to Developer Settings:**  
   - Click your user avatar (usually in the top-right corner).
   - Select **Settings** from the dropdown menu.
   - Go to the **Developers** section.
3. **Generate a New Token:**  
   - In the Developers section, look for the option to **Generate new token**.
   - Click it to create your personal access token.
4. **Store the Token Securely:**  
   - Copy the generated token and keep it safe—do not share it or commit it to public repositories.
   - You can use it directly in your API requests as an authentication header.

> [!WARNING]
> This token is only valid for your own account and does not allow access to other users’ data unless you use OAuth for broader permissions.


```clojure
(require '[discogs.client :as dc])

(def client (dc/mk-client "pat_ZrUgstGNqDTDWMGWChuPXrH"))
```

### Monitor Quota Usage

You can monitor API usage by adding client request callback as follow:

```clojure
(require '[discogs.reporter :as dr])

(dr/set-quota-reporter-callback! client (fn [quota] (println "Quota:" quota)))

;; Or use default one

(dr/set-quota-reporter-callback! client dr/default-reporter)
```

Default callback will monitor when quota is below 10 left. (Logged as a warn)


### Interact With Database

> TODO

### Commons Algorithms

> TODO

## Disclaimer of Affiliation with Discogs

This software/library is an independent project developed by its author(s) and is not affiliated with, sponsored by,
or endorsed by Discogs or any of its subsidiaries. "Discogs" is a registered trademark of Discogs Inc.

The use of the "Discogs" name and any related trademarks within this project is for identification and descriptive purposes only
and does not imply any connection, endorsement, or approval by Discogs Inc.

## License

Copyright © 2025 iomonad

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

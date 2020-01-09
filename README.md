# CloudServer

## 動機

隨著科技網路的迅速發展，人們使用雲端上傳檔案也越來越普及，導致雲端商業產品也越來越多，因此我們想要架設屬於自己的私有雲端伺服器後，就可以將檔案上傳至自己的雲端伺服器上。

## 硬體材料

| 名稱           | 數量 | 來源     |
| -------------- | ---- | -------- |
| Raspberry Pi 3 | 2    | MOLi     |
| 2TB 隨身碟     | 1    | 自備 |

## 系統架構圖

![](https://i.imgur.com/1gUQXRQ.png)


## 實體圖

![](https://i.imgur.com/LRJNnCP.png)


## Telegram Bot

### Bot_id

@NCNU_cloud_bot

### Bot 指令

- `/help`
    - 查詢可使用的指令
- `/space`
    - 查詢空間大小
- `/ip`
    - 查詢IP Address

## 操作畫面


### NextCloud


![](https://i.imgur.com/XbECvcp.png)

### Telegram

![](https://i.imgur.com/30paVMr.png)

## 架設流程

### Raspberry Pi-A

#### 自動掛載隨身碟

1. 查看所有分割區

   ```
   sudo fdisk -l
   ```

2. 要在 Linux 底下掛載 exFAT 格式的硬碟，必須先安裝 `exfat-fuse` 、`exfat-utils`

   ```
   sudo apt -y install exfat-fuse exfat-utils
   ```

3. 查看所有硬碟的 UUID

   ```
   ls -l /dev/disk/by-uuid/
   ```

4. 建立目錄並設定權限

   ```
   sudo mkdir /media/USB
   sudo chmod 770 /media/USB
   ```

5. 查看 www-data 的 user id 及 group id 

   ```
   grep www-data /etc/passwd
   ```

6. 進行掛載

   ```
   sudo mount -t exfat -o uid=33,gid=33,umask=007 /dev/sda1 /media/USB
   ```

7. 設定自動掛載

   ```
   sudo vim /etc/fstab
   ```
   
   - 在檔案裡新增：
   
     ```
     UUID=6060-75EF /media/USB exfat uid=33,gid=33,umask=007 0 0
     ```

#### 架設 NFS Server

1. 安裝NFS伺服器

   ```
   sudo apt -y install nfs-kernel-server
   ```

2. 確定NFS伺服器是否有確實安裝成功 

   ```
   sudo netstat -ntulp | grep :2049
   ```

3. 設定要分享的目錄

   ```
   sudo vim /etc/exports
   ```

   - 在檔案裡新增：

     ```
     /media/USB 192.168.31.51(rw,sync,no_subtree_check)
     ```

4. 重新載入設定檔

   ```
   sudo exportfs -r
   ```

5. 查看目錄的分享狀態 

   ```
   sudo exportfs
   ```

### Raspberry Pi-B

#### NFS Client

1. 安裝NFS客戶端

   ```
   sudo apt -y install nfs-common
   ```

2. 新增目錄

   ```
   sudo mkdir /mnt/nfs
   ```

3. 掛載NFS

   ```
   sudo mount -t nfs 192.168.31.222:/media/USB /mnt/nfs
   ```

4. 自動掛載NFS

   ```
   sudo vim /etc/rc.local
   
   ```
   
      - 在檔案裡新增：
          
          ```
          mount -t nfs 192.168.31.222:/media/USB /mnt/nfs
          ```
          
#### NextCloud

1. 安裝 nginx

   ```
   sudo apt -y install nginx
   ```

2. 啓動nginx

   ```
   sudo systemctl start nginx
   sudo systemctl enable nginx
   ```
   
3. 確認nginx是否啓動成功

   ```
   sudo systemctl status nginx
   sudo netstat -ntulp | grep :80
   ```

4. 安裝 mariadb-server、mariadb-client

   ```
   sudo apt -y install mariadb-server mariadb-client
   ```

5. 啓動 mariadb-server

   ```
   sudo systemctl start mariadb
   sudo systemctl enable mariadb
   ```

6. 確認mariadb是否啓動成功

   ```
   sudo systemctl status mariadb
   sudo netstat -ntulp | grep :3306
   ```

7. 提高 MariaDB 安全性

   ```
   sudo mysql_secure_installation
   ```

   ```
   NOTE: RUNNING ALL PARTS OF THIS SCRIPT IS RECOMMENDED FOR ALL MariaDB
         SERVERS IN PRODUCTION USE!  PLEASE READ EACH STEP CAREFULLY!
   
   In order to log into MariaDB to secure it, we'll need the current
   password for the root user.  If you've just installed MariaDB, and
   you haven't set the root password yet, the password will be blank,
   so you should just press enter here.
   
   Enter current password for root (enter for none): （請按Enter）
   OK, successfully used password, moving on...
   
   Setting the root password ensures that nobody can log into the MariaDB
   root user without the proper authorisation.
   
   Set root password? [Y/n] y（輸入y）
   New password:（輸入密碼）
   Re-enter new password: （輸入密碼）
   Password updated successfully!
   Reloading privilege tables..
    ... Success!
   
   
   By default, a MariaDB installation has an anonymous user, allowing anyone
   to log into MariaDB without having to have a user account created for
   them.  This is intended only for testing, and to make the installation
   go a bit smoother.  You should remove them before moving into a
   production environment.
   
   Remove anonymous users? [Y/n] （請按Enter）
    ... Success!
   
   Normally, root should only be allowed to connect from 'localhost'.  This
   ensures that someone cannot guess at the root password from the network.
   
   Disallow root login remotely? [Y/n] （請按Enter）
    ... Success!
   
   By default, MariaDB comes with a database named 'test' that anyone can
   access.  This is also intended only for testing, and should be removed
   before moving into a production environment.
   
   Remove test database and access to it? [Y/n] （請按Enter）
    - Dropping test database...
    ... Success!
    - Removing privileges on test database...
    ... Success!
   
   Reloading the privilege tables will ensure that all changes made so far
   will take effect immediately.
   
   Reload privilege tables now? [Y/n] （請按Enter）
    ... Success!
   
   Cleaning up...
   
   All done!  If you've completed all of the above steps, your MariaDB
   installation should now be secure.
   
   Thanks for using MariaDB!
   ```

10. 安裝 PHP7.3

    ```
    sudo apt -y install php7.3-fpm php7.3-mbstring php7.3-xmlrpc php7.3-soap php-apcu php-smbclient php7.3-ldap php-redis php7.3-gd php7.3-xml php7.3-intl php7.3-json php-imagick php7.3-mysql php7.3-cli php7.3-ldap php7.3-zip php7.3-curl
    ```

9. 編輯 /etc/php/7.3/fpm/php.ini 檔案

   ```
   sudo vim /etc/php/7.3/fpm/php.ini
   ```

   - 修改參數：

   ```
   file_uploads = On
   allow_url_fopen = On
   memory_limit = 256M
   upload_max_filesize = 100G
   display_errors = Off
   cgi.fix_pathinfo = 0
   date.timezone = Asia/Taipei
   ```

14. 啓動 php7.3-fpm

    ```
    sudo systemctl start php7.3-fpm
    sudo systemctl enable php7.3-fpm
    ```
    
13. 確認 php7.3-fpm 是否啓動成功

    ```
    sudo systemctl status php7.3-fpm
    ```

14. 下載 nextcloud-17.0.2.zip 

    ```
    wget https://download.nextcloud.com/server/releases/nextcloud-17.0.2.zip
    ```

15. 解壓縮 nextcloud-17.0.2.zip

    ```
    sudo unzip nextcloud-17.0.2.zip -d /var/www/html/
    ```

16. /var/www/html/nextcloud/ 的權限設定

    ```
    sudo chown -R www-data:www-data /var/www/html/nextcloud/
    sudo chmod -R 755 /var/www/html/nextcloud/
    ```

17. 新增 nextcloud 資料庫及新增使用者

    ```
    sudo mariadb
    create database nextcloud;
    create user 使用者名稱@localhost identified by '密碼';
    grant all privileges on nextcloud.* to 使用者名稱@localhost identified by '密碼';
    flush privileges;
    exit;
    ```

16. 設定/etc/hosts

    ```
    sudo vim /etc/hosts
    ```

    - 在檔案裡新增

      ```
      127.0.0.1 nextcloud.ncnulsa.idv.tw
      ```

19. 刪除 nginx 預設的 VirtualHost 

    ```
    sudo rm /etc/nginx/sites-available/default
    sudo rm /etc/nginx/sites-enabled/default
    ```

18. 設定 nginx 的 VirtualHost 名稱爲 nextcloud

    ```
    sudo vim /etc/nginx/sites-available/nextcloud
    ```

    - nextcloud 的內容

      ```
      server {
          listen 80;
          listen [::]:80;
          root /var/www/html/nextcloud;
          index  index.php index.html index.htm;
          server_name  nextcloud.ncnulsa.idv.tw;
      
          client_max_body_size 512M;
          fastcgi_buffers 64 4K;
      
          location / {
              rewrite ^ /index.php$request_uri;
          }
      
          location ~ ^/(?:build|tests|config|lib|3rdparty|templates|data)/ {
              deny all;
          }
          location ~ ^/(?:\.|autotest|occ|issue|indie|db_|console) {
              deny all;
          }
      
          location ~ ^/(?:index|remote|public|cron|core/ajax/update|status|ocs/v[12]|updater/.+|ocs-provider/.+)\.php(?:$|/) {
              fastcgi_split_path_info ^(.+?\.php)(/.*)$;
              include fastcgi_params;
              fastcgi_param SCRIPT_FILENAME $document_root$fastcgi_script_name;
              fastcgi_param PATH_INFO $fastcgi_path_info;
              fastcgi_pass unix:/var/run/php/php7.3-fpm.sock;
              fastcgi_intercept_errors on;
              fastcgi_request_buffering off;
          }
      
          location ~ ^/(?:updater|ocs-provider)(?:$|/) {
              try_files $uri/ =404;
              index index.php;
          }
      
          location ~ \.(?:css|js|woff|svg|gif)$ {
              try_files $uri /index.php$request_uri;
              add_header Cache-Control "public, max-age=15778463";
              access_log off;
          }
      
          location ~ \.(?:png|html|ttf|ico|jpg|jpeg)$ {
              try_files $uri /index.php$request_uri;
              # Optional: Don't log access to other assets
              access_log off;
          }
      }
      ```

21. 啓用名爲 nextcloud 的  VirtualHost 

    ```
    sudo ln -s /etc/nginx/sites-available/nextcloud /etc/nginx/sites-enabled/
    ```

22. 設定防火牆

    ```
    sudo iptables -I INPUT -p tcp --dport 80 -j ACCEPT
    ```

23. 重新啓動 nginx

    ```
    sudo systemctl restart nginx
    ```

24. 進入網站頁面，輸入資料

    ![](https://i.imgur.com/sCrArOF.png)

    
23. 登入成功

    ![](https://i.imgur.com/mY8MzXJ.png)


## 遇到的問題

1. 不能在/etc/fstab設定自動掛載
    - 解決：改在/etc/rc.local裡面進行設定


## 分工

**107321522 陳奕哲**

- 題目討論
- 文件撰寫
- 簡報製作
- 系統實作

**107321519 彭梓宸**

- 題目討論
- 程式撰寫
- 文件撰寫
- 簡報製作
- 系統實作

## 投影片

https://drive.google.com/open?id=1VGoUkWPDGbnN_9h7pJvGVVB5L5QNFMgf

## 參考資料

https://linux.vbird.org/linux_server/centos6/0330nfs.php#nfsserver_exports

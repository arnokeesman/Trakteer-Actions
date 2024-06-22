# Trakteer Mod
This mod is for a Trakteer.Id intregation which is an Indonesian Company.
[English Explanation](https://github.com/arnokeesman/Trakteer-Actions/blob/main/README.md)

Tutorial video bahasa Indonesia: 

TrakteerMod adalah Mod untuk Fabric yang di buat untuk intregasi Donasi Trakteer.id ke World Minecraft World atau server. Mod ini memungkinkan pemain untuk menciptakan pengalaman interaktif dan menarik berdasarkan donasi realtime dari platform Trakteer. Dengan mengonfigurasi action, pemilik server dapat menyesuaikan cara lingkungan Minecraft mereka merespons berbagai event donasi, meningkatkan gameplay, dan mendorong interaksi yang lebih dinamis antara Supporter dan Player.

Fitur utama TrakteerMod:

- Event yang dapat dikonfigurasi: Tentukan event spesifik yang harus dilakukan dalam game berdasarkan properti donasi seperti nama pendukung, jumlah donasi, dan pesan khusus.
- Mode Realtime dan Mode Test: Pilih antara mode langsung (Realtime/Aktif) untuk terhubung dengan Server Trakteer atau mode uji (test) untuk disimulasikan sumbangan untuk tujuan konfigurasi dan pengujian.
- Kondisi Fleksibel: Gunakan berbagai operator untuk menetapkan kondisi tindakan, memastikan bahwa donasi memicu peristiwa dalam game yang diinginkan hanya berdasarkan kriteria tertentu.

## Command options

### Options

Untuk mengecek status/isi dari config, gunakan `/trakteermod <optionName>`  
Untuk mengubah status/isi dari config, gunakan `/trakteermod <optionName> <value>`

| Option Name | Values/Type       | Default Value | Description                        | Role     |
|-------------|-------------------|---------------|------------------------------------|----------|
| interval    | integer (seconds) | 0 (disabled)  | Interval untuk server akan cek server trakteer.id    | Admin    |
| mode        | test\|real        | test          | Mode yang digunakan saat ini.  | Admin    |
| apiKey      | string            | none          | API-Key yang digunakan | All Players |

## Pembuatan Config

Bagian ini hanya untuk Owner server atau Admin. 
Config bisa di reload dengan `/trakteermod reload`

Kalian bisa membuat Config secara manual di text editor seperti notepad atau menggunakan [CONFIG GENERATOR](https://trakteerconfig.pages.dev/).

### Syntax

- Membuat Action baru  
  `### <Name of action>`
- Tandai Action sebagai aktif saat player penerima offline  
  `:offline`
- Tambahkan kondisi ke Action  
  `:if <donation property> <operator> <value>`
- Tambahkan Action didalam Action lain (Nesting)  
  `:include <Name of Action to include>`
- Command untuk menjalankan Event  
  Bisa menggunakan Command Vanilla Minecraft atau Command Mod.  
  Cek config contoh dibawah

### Donation properties
Data yang bisa di gunakan dari pemberi donasi.

| Name              | Description               |
|-------------------|---------------------------|
| `supporter_name`  | Nama Pendukung     |
| `support_message` | Pesan Pendukung   |
| `amount`          | Jumlah Rupiah yang didonasikan            |
| `unit_name`       | Nama Unit Donasi     |
| `quantity`        | Jumlah Unit Donasi   |
| `receiver`        | Player penerima donasi |

### Requirement operators

| Operator   | Alias | Description                                                      |
|------------|-------|------------------------------------------------------------------|
| `contains` |       | Mengandung                |
| `equals`   | `=`   | Sama/Persis           |
| `gte`      | `>=`  | Lebih Besar atau sama |
| `lte`      | `<=`  | Lebih Kecil atau sama    |
| `gt`       | `>`   | Lebih Besar            |
| `lt`       | `<`   | Lebih Kecil               |

### Contoh config
Action pertama `### default` tidak bisa di hilangkan, dan syntax `:offline` harus di tambahkan!

```
### default
:offline
say Terimakasih, {supporter_name} donasikan {amount}!

### creeper
:if support_message contains boom
:if amount >= 10000
:include default
execute at {receiver} run summon minecraft:creeper ~ ~ ~ {ExplosionRadius:5,ignited:1}
say Awas, {receiver}! {supporter_name} summon Creeper!

### splash
:if support_message contains splash
execute at {receiver} run setblock ~ ~1 ~ minecraft:water
say {supporter_name} memberikan {receiver} a splash surprise!

### lightning
:if support_message contains strike
:if amount >= 2000
execute at {receiver} run summon minecraft:lightning_bolt
say {supporter_name} menyambar {receiver} dengan petir!

### makan
:if support_message contains feast
:if quantity >= 5
give {receiver} minecraft:cooked_beef 10
say {supporter_name} memberikan makanan untuk {receiver}!

### armor_up
:if support_message contains armor
give {receiver} minecraft:diamond_chestplate
give {receiver} minecraft:diamond_leggings
give {receiver} minecraft:diamond_boots
give {receiver} minecraft:diamond_helmet
say {supporter_name} memberikan {receiver} satu set diamond armor!

### fireworks
:if amount >= 5000
:include default
execute at {receiver} run summon firework_rocket ~ ~5 ~ {LifeTime:0,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:2,Explosions:[{Type:1,Flicker:0,Trail:0,Colors:[I;11743532,14602026],FadeColors:[I;2437522]},{Type:1,Flicker:0,Trail:0}]}}}}
say Pertunjukan kembang api untuk {receiver}, dari {supporter_name}!

```

## Testing API

Untuk mengetest mod tanpa menggunakan real-donasi ke trakteer, kalian bisa menggunakan testing-api yang sudah kita buat. Kamu bisa menjalankan ini di komputer local kalian dan membuat config file di singleplayer. Penjelasan lengkap dan lain lain bisa di temukan di [testing-api branch](https://github.com/arnokeesman/Trakteer-Actions/tree/testing-api)


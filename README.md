# TubeMe Android — Native Kotlin + Jetpack Compose

Bu **to'liq native Android ilova**, sizning TubeMe veb-loyihangizdan portlangan. WebView yo'q — barchasi Kotlin/Compose'da yozilgan, Google Sheets'dan to'g'ridan-to'g'ri ma'lumotlar oladi.

## ✨ Asosiy xususiyatlar

- **100% Native** — Kotlin + Jetpack Compose, Material 3
- **Google Sheets integratsiyasi** — xuddi web versiyadagi GVIZ JSON endpoint, bir xil Sheet ID:
  - Videos (GID 0)
  - Collections (GID 1794713448)
  - News (GID 1389725413)
- **Aqlli pleyer** (`SmartPlayer`):
  - YouTube havolalari → **YouTubePlayerView** (rasmiy IFrame Player)
  - `.m3u8` havolalari → **ExoPlayer + HLS**
  - `.mp4 / .mkv / .webm / boshqa CDN** → **ExoPlayer Progressive**
  - URL'ga qarab avtomatik tanlash
- **AMOLED qora mavzu** — sizning web variantingiz bilan bir xil rang sxemasi
- **Offline cache** — DataStore'da sevimlilar, saqlanganlar, tarix, davom ettirish, progress
- **3 ta til** — O'zbek (default), Русский, English
- **8 ta to'liq sahifa**:
  - 🏠 Home (Hero karusel + bo'limlar)
  - 📚 Collections (To'plamlar)
  - 🔍 Search (Qidiruv + filtrlar)
  - ❤️ Favorites (4 tab: Liked / Saved / Continue / History)
  - 👤 Profile (Sozlamalar + til + sifat + tezlik)
  - 🎬 Video (Pleyer + harakatlar + shunga o'xshashlar)
  - 📂 Section (bo'lim ichi)
  - 📺 Collection Detail (fasllar + epizodlar)

## 🛠 APK build qilish

### 1-usul: Android Studio (eng oson, **tavsiya etiladi**)

1. Android Studio Hedgehog yoki keyingisini o'rnating: https://developer.android.com/studio
2. Android Studio'ni oching → **File → Open** → bu papkani tanlang (`TubeMe-Android`)
3. Birinchi sinxronizatsiya 2-5 daqiqa davom etadi (kutubxonalarni yuklaydi)
4. **Build → Build Bundle(s) / APK(s) → Build APK(s)** menyusini bosing
5. APK yarating: `app/build/outputs/apk/debug/app-debug.apk`
6. APK'ni telefoningizga ko'chiring va o'rnating

### 2-usul: Buyruq satridan

```bash
# JDK 17 va Android SDK kerak ($ANDROID_HOME o'rnatilgan bo'lsin)
chmod +x gradlew
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

### 3-usul: Online (Codemagic, GitHub Actions)
GitHub'ga yuklang va `android-build` workflow ishlatamiz.

## 📦 Texnologiyalar

| Komponent | Kutubxona |
|---|---|
| UI | Jetpack Compose, Material 3 |
| Navigatsiya | androidx.navigation:compose |
| Tarmoq | Retrofit 2 + OkHttp |
| Rasm yuklash | Coil |
| Video pleyer | ExoPlayer (Media3) + AndroidYouTubePlayer |
| Saqlash | DataStore Preferences |
| Async | Kotlin Coroutines + StateFlow |

## 📊 Google Sheets ustunlari

### Videos jadvali (GID 0)
| A: section | B: type | C: title | D: description | E: thumbnail | F: video | G: genre | H: language | I: country | J: year | K: rating | L: videoId | M: hero |

### Collections jadvali (GID 1794713448)
| A: title | B: description | C: thumbnail | D: season | E: videoIds (vergul bilan) | F: collectionId |

### News jadvali (GID 1389725413)
| A: title | B: description | C: image |

## 🎯 Asosiy fayllar

- `data/api/SheetsClient.kt` — Sheet ID va GID'lar
- `data/api/SheetsParser.kt` — GVIZ JSON → ob'ektlar
- `data/repository/TubeMeRepository.kt` — Yagona ma'lumotlar manbai
- `player/SmartPlayer.kt` — YouTube vs ExoPlayer aqlli tanlovi
- `ui/TubeMeRoot.kt` — Navigatsiya skeleton

## 🔧 Sheet ID o'zgartirish

`app/src/main/java/uz/tubeme/app/data/api/SheetsApi.kt` faylini oching va `SHEET_ID` ni o'zgartiring.

## 📱 Min Android versiyasi: 7.0 (API 24) — Bu Android 7.0 va undan yuqori barcha qurilmalarda ishlaydi.

---
TubeMe v1.0.0 — Native Android (Kotlin + Compose)

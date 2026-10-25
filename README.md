# Cosmetics (Fabric 1.21.4, client-side)

Faqat o'zingizga ko'rinadigan cosmetic mod. Hozir ichida: **Gucci Backpack** (orqa slot).

- Menyuni ochish: **K** tugmasi (Controls -> "Cosmetics" bo'limida o'zgartirish mumkin)
- Sozlamalar: `.minecraft/config/cosmetics.json` (`sizeMultiplier` bilan hajmni o'zgartirish mumkin)
- O'rnatish: `cosmetics-1.0.0.jar` ni `.minecraft/mods` papkasiga tashlang. Kerak: Fabric Loader 0.16+, **Fabric API**, Java 21.

## Jar'ni build qilish

### A) GitHub orqali (kompyuterga hech narsa o'rnatish shart emas)
1. github.com da bepul akkaunt oching, **New repository** -> nom: `cosmetics-mod` -> Create.
2. **uploading an existing file** havolasini bosing va shu papka ichidagi HAMMA fayl va papkalarni sudrab tashlang
   (`.github` papkasi ham bo'lishi shart!). **Commit changes** bosing.
3. Repo'da **Actions** bo'limiga o'ting: "Build mod jar" ishga tushadi (2-5 daqiqa).
4. Yashil galochka chiqqach, shu run'ni oching va pastdagi **Artifacts -> cosmetics-mod-jar** ni yuklab oling.
   Zip ichida `cosmetics-1.0.0.jar` bor (`-sources.jar` kerak emas).

### B) Kompyuterda
JDK 21 o'rnating, shu papkada: `./gradlew build` (Windows: `gradlew.bat build`). Jar: `build/libs/cosmetics-1.0.0.jar`.

## Yangi cosmetic qo'shish
1. Model `.quads` formatida `src/main/resources/assets/cosmetics/models/` ga, tekstura `textures/cosmetic/` ga.
2. `CosmeticRegistry.init()` ichida yana bitta `register(new Cosmetic(...))`.
3. `lang/en_us.json` ga `"cosmetic.cosmetics.<id>": "Nomi"` qo'shing.

## Qiymatlarni sozlash (backpack joyi)
`CosmeticRegistry.java`: `0f, 0.5f, 2.6f` (x, y, z piksel) joylashuv, `0.5f` o'lcham.

## Credits
Model: "Gucci backpack" (Cubik Studio formatida, Sketchfab'dan). Muallif va litsenziyani shu yerga yozing.

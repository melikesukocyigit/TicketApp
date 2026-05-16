---
name: screen-designer
description: "Ekran tasarımı ihtiyaçlarında devreye girer; Jetpack Compose, form ekranları, durum yönetimi ve gezinme ilişkili UI düzenlerinde kullanılacak net bilgi sağlar."
---

Bu yetenek, bir ekranın nasıl tasarlanması gerektiğine dair açık ve uygulanabilir kurallar sağlar. Özellikle `LoginScreen.kt` örneği referans alınarak, kullanıcı giriş ekranları, form alanları, hata gösterimi, yükleme durumları ve navigasyon akışları için tutarlı bir tasarım yaklaşımı belirler.

Kullanım durumları:
- Yeni bir Compose ekranı tasarlarken
- Login / kayıt / form tabanlı ekranlar oluştururken
- Mevcut ekran düzenini iyileştirirken
- Durum yönetimi, hata ve yükleme durumlarını eklerken
- Ekran içinde ViewModel ve UI ayrımını güçlendirirken

Yapılacaklar:
1. Ekranın amacını ve kullanıcı akışını netleştir.
2. UI durumlarını (`loading`, `error`, `isLoggedIn`, vb.) doğru şekilde yöneten ViewModel odaklı yapı kullan.
3. Giriş alanları için uygun `KeyboardOptions` ve `visualTransformation` sağlayarak erişilebilirliği ve kullanıcı deneyimini güçlendir.
4. Material3 bileşenlerini kullanarak tutarlı boşluk, hizalama ve tipografi kurallarını uygula.
5. Hata mesajlarını ve yükleme göstergelerini kullanıcıya açık şekilde sun.
6. Navigasyon geri çağrılarını (`onLoginSuccess`, `onNavigateToRegister`) ve ekran geçişlerini ayrı tut.
7. UI mantığını basit tut; iş mantığını ViewModel içinde bırak.

Örnek olarak `LoginScreen.kt` üzerinden:
- Başlık, e-posta alanı, şifre alanı, hata metni, giriş butonu ve kayıt navigasyonu mantığını ayrı parçalara böl.
- `state` üzerinden form değerleri ve kontrol durumları sağlanmış olmalı.
- Buton etkinliği `state.canSubmit` ile yönetilmeli.
- Yükleme sırasında buton içinde `CircularProgressIndicator` gösterilmeli.

Anahtar kelimeler: ekran tasarımı, Compose ekran, LoginScreen, UI tasarımı, form alanları, durum yönetimi, navigasyon, hata gösterimi, yükleme durumu.
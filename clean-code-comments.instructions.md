---
description: Bu dosya projede yorum satırlarıyla alakalı "Clean Code" prensiplerini içerir.
---

# Clean Code: Yorum Satırları Kuralları

Bu dosya, Robert C. Martin'in "Clean Code" kitabındaki "Comments" bölümündeki tüm kuralları özetler. Kod yazarken yorum satırlarını kullanırken bu prensipleri takip edin.

## Genel İlkeler

### 1. Yorumlar Kötü Kodu Telafi Etmez
- Yorumlar, kötü yazılmış kodu düzeltmek için kullanılmamalıdır.
- Kodunuzu temiz ve anlaşılır hale getirin; yorumlara güvenmeyin.
- Eğer yorum yazmanız gerekiyorsa, bu kodun iyileştirilmesi gerektiğini gösterir.

### 2. Kodu Kendin Açıkla
- Kodunuz, niyetinizi açıkça ifade etmelidir.
- İyi isimlendirme ve yapı ile kodu kendi kendini açıklayıcı hale getirin.
- Yorumlar, kodun ne yaptığını açıklamak yerine neden yaptığını açıklamalıdır.

## İyi Yorumlar

### 1. Yasal Yorumlar
- Telif hakkı ve lisans bilgileri için kullanılır.
- Örnek: `// Copyright (C) 2023 Turkcell`

### 2. Bilgilendirici Yorumlar
- Temel bilgileri sağlar, kodun kendisinden çıkarılamayan.
- Örnek: `// Returns an instance of the Responder being tested.`

### 3. Niyet Açıklaması
- Kodun arkasındaki nedeni açıklar.
- Örnek: `// The result is cached to improve performance.`

### 4. Uyarı Yorumları
- Gelecekteki geliştiricilere uyarı verir.
- Örnek: `// Don't run unless you have some time to kill.`

### 5. TODO Yorumları
- Gelecekte yapılacak işleri işaret eder.
- Örnek: `// TODO: Remove this code after the next release.`

### 6. Genişletme Yorumları
- Kodun önemini vurgular.
- Örnek: `// This is the main entry point for the application.`

### 7. Genel API'lerde Javadoc
- Public API'ler için standart Javadoc kullanın.
- Parametreler, dönüş değerleri ve istisnalar açıklanmalıdır.

## Kötü Yorumlar

### 1. Mırıldanma
- Anlamsız veya belirsiz yorumlar.
- Örnek: `// add the value` (ne değeri?)

### 2. Gereksiz Yorumlar
- Kodun zaten söylediğini tekrarlar.
- Örnek: `i++; // increment i`

### 3. Yanlış Yönlendirme Yorumları
- Yanlış bilgi verir veya güncel değildir.
- Kod değiştiğinde yorumları güncelleyin.

### 4. Zorunlu Yorumlar
- Şirket politikaları nedeniyle yazılan ama faydasız yorumlar.
- Bu tür yorumlardan kaçının.

### 5. Günlük Yorumları
- Tarih ve yazar bilgilerini içerir.
- Örnek: `// Changed by Halit on 2023-10-01`
- Versiyon kontrol sistemi bunu zaten takip eder.

### 6. Gürültü Yorumları
- Anlamsız veya gereksiz.
- Örnek: `/** Default constructor */`

### 7. Korkutucu Gürültü
- Çok fazla yıldız veya çizgi ile dikkat çekmeye çalışır.
- Basit tutun.

### 8. Fonksiyon veya Değişken Yerine Yorum Kullanma
- Kodun ne yaptığını açıklamak yerine, kodu yeniden düzenleyin.
- Örnek: Uzun bir yorum yerine, kodu küçük fonksiyonlara bölün.

### 9. Konum İşaretçileri
- Bölümleri ayırmak için kullanılan yorumlar.
- Örnek: `// Actions ////////////////////////`
- Kod düzenleme araçları bunu yapar.

### 10. Kapanış Parantez Yorumları
- `}` sonrası yorumlar.
- Örnek: `} // end if`
- Modern IDE'ler bunu gösterir.

### 11. Atıflar ve İmza Yorumları
- Kimin yazdığını belirtmek için.
- Versiyon kontrolü yeterli.

### 12. Yorumlanmış Kod
- Eski kod parçalarını yorum içine almak.
- Silin veya versiyon kontrolünden geri getirin.

### 13. HTML Yorumları
- Kod içinde HTML etiketleri kullanmak.
- Özellikle Javadoc'ta kaçının.

### 14. Çok Fazla Bilgi
- İlgili olmayan detaylar.
- Yorumları kısa ve ilgili tutun.

## Uygulama Kuralları

- Yorumları taze tutun: Kod değiştiğinde yorumları güncelleyin.
- Kısa ve öz olun.
- Kodun niyetini açıklamaya odaklanın.
- Mümkün olduğunca yorum kullanmaktan kaçının; kodu konuşur hale getirin.

Bu kurallar, kodunuzun kalitesini artırmak ve bakımını kolaylaştırmak için önemlidir.
## 6.4.0 (xx.xx.xxxx)
* OGC outputSchema wird unterstützt (#5223). outputSchema muss als Attribut bei Anfragen mitgegeben werden.
  * outputSchema standard ist GMD.
  * outputSchema OGC kann als Attribut in einer GetRecords, GetRecordById als URI mitgegeben werden.
  Bsp: <GetRecordById service="CSW" version="2.0.2" outputFormat="application/xml" outputSchema="OGC"
  * Die ausgegebenen Dokumente sind dann im OGC-Format, dublin core. Die übliche ElementSetNames sind unterstützt: full, summary, brief.


## 6.3.0 (17.10.2023)


### Bugfixes

* ISO-XML Ausgabe im Datenfinder und Datenrepository nicht valide  (#5177)
    
## 6.1.0 (14.04.2023)





### Bugfixes

* Kritische Sicherheitslücke: snakeyaml + weitere  (#4972)
* Override-Konfiguration wird nicht eingelesen  (#4939)
    
## 6.0.0 (13.01.2023)

### Features

* IGE: Regionalschlüssel erfassen - Minimallösung (#3928)
* Aktualisierung auf JAVA 17 (LTS): Umsetzung (#3324)

### Bugfixes

* Nicht thread sichere Bearbeitung von Filter Requests in CSW Schnittstelle  (#4354)
* Verbesserung der Fehlermeldung im Interface CSW  (#4352)
    
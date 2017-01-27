# external-ip-monitor

Aplikacja klient-serwer umożliwiająca wykorzystanie monitora podłączonego do komputera klienta jako ekranu rozszerzającego pulpit serwera z wykorzystaniem sieci TCP/IP.

## Wymagania

* Serwer
  * Ubuntu 16.10
  * maim (*sudo apt-get install maim*)
  * XFixes (*sudo apt-get install libx11-protocol-other perl*)
  * Java 1.8
* Klient
  * Windows 10
  * .NET Framework 4.5.2
  
## Ograniczenia
  
* Wysokość wyświetlanego obrazu na kliencie odpowiada składowej wysokości rozdzielczości serwera (brak możliwości odseparowania rozdzielczości)
* Rozszerzony obszar roboczy jest spójny, tj. w przypadku uruchomienia jakiejkolwiek aplikacji w trybie pełnego ekranu, zostanie ona rozciągnięta na obydwa monitory

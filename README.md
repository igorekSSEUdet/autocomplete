**консольное Java-приложение (JDK 11), позволяющее быстро искать
данные аэропортов по вводимому пользователем названию аэропорта и фильтрам**

Данные для программы берутся из файла. В нем находится таблица аэропортов
со свойствами в формате CSV. Название аэропорта — 2 колонка. За что отвечают другие
колонки — не важно, на них навешиваются фильтры
Фильтры могут быть — отношения равенства: равно (=), не равно (<>), больше (>), меньше (<).
Фильтр передается в формате:
column[1]>10 & column[5]=’GKA’ || column[<номер колонки с 1>]<операция сравнения>...

Фильтры могут соединяться отношением И (&) и ИЛИ (||). Также могут участвовать скобки для
обозначения приоритета и группировки. Отношение И имеет более высокий приоритет
нежели ИЛИ. Фильтр может быть не указан

*Принятые решения для реализации ТЗ:*

1.Для парсинга фильтра использовал регулярные выражения и [алгоритм обратной польской записи](https://ru.wikipedia.org/wiki/Обратная_польская_запись)

2.Для быстрого поиска по городам использовал TreeMap для индексирования строчек,бинарный поиск и [WeakHashMap](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/WeakHashMap.html) для кэширования запросов

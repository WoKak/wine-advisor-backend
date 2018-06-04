# -*- coding: utf-8 -*-
import mysql.connector
import unidecode

cnx = mysql.connector.connect(user='root', password='', host='127.0.0.1', database='wina')
cursor = cnx.cursor()
arfffile = open("latest.arff", "w",encoding='utf-8')
query = ("SELECT * FROM wino")
szczepyset = set()
rodzajset = set()
wytrawnoscset = set()
krajset = set()
przeznaczenieset = set()

cursor.execute(query)
data = []
for (id, alkohol, szczepy, rodzaj, wytrawnosc, kraj, przeznaczenie) in cursor:
    kraj = kraj.replace(" ", "-")
    przeznaczenie = przeznaczenie.replace(" ", "-")
    data.append((str(alkohol) + "," + szczepy + "," + rodzaj + "," + wytrawnosc + "," + kraj + "," + przeznaczenie + "\n"))
    szczepyset.add(szczepy)
    rodzajset.add(rodzaj)
    wytrawnoscset.add(wytrawnosc)
    krajset.add(kraj)
    przeznaczenieset.add(przeznaczenie)

cursor.close()
cnx.close()

szczepystring = szczepyset.pop()
while szczepyset:
    szczepystring = szczepystring + "," + szczepyset.pop()

rodzajstring = rodzajset.pop()
while rodzajset:
    rodzajstring = rodzajstring + "," + rodzajset.pop()

wytrawnoscstring = wytrawnoscset.pop()
while wytrawnoscset:
    wytrawnoscstring = wytrawnoscstring + "," + wytrawnoscset.pop()

krajstring = krajset.pop()
while krajset:
    krajstring = krajstring + "," + krajset.pop()

przeznaczeniestring = przeznaczenieset.pop()
while przeznaczenieset:
    przeznaczeniestring = przeznaczeniestring + "," + przeznaczenieset.pop()



arfffile.write("@RELATION wine\n")
arfffile.write("\n@ATTRIBUTE alcohol REAL")
arfffile.write("\n@ATTRIBUTE strain {" + unidecode.unidecode(szczepystring) + "}")
arfffile.write("\n@ATTRIBUTE kind {" + unidecode.unidecode(rodzajstring) + "}")
arfffile.write("\n@ATTRIBUTE dryness {" + unidecode.unidecode(wytrawnoscstring) + "}")
arfffile.write("\n@ATTRIBUTE origin {" + unidecode.unidecode(krajstring) + "}")
arfffile.write("\n@ATTRIBUTE classs {" + unidecode.unidecode(przeznaczeniestring) + "}")

arfffile.write("\n\n@DATA\n")
for wino in data:
    arfffile.write(unidecode.unidecode(wino))

arfffile.close()
REM Segundo trabalho. 
REM testa a geração de código 

del ok-*.txt
del er-*.txt
del *.cpp
del z.txt
del r.txt
del ok-*.exe
del er-*.exe

rem del ..\..\t\ok-*.txt
rem del ..\..\t\er-*.txt
rem del ..\..\t\ok-*.exe
rem del ..\..\t\er-*.exe
rem del ..\..\tests\ok-*.txt
rem del ..\..\tests\er-*.txtcd
rem del ..\..\tests\ok-*.exe
rem del ..\..\tests\er-*.exe
rem del ..\..\tests\ok-*.c


rem pause


java -cp . comp.Comp ..\..\tests\OK-GER01.KRA
java -cp . comp.Comp ..\..\tests\OK-GER02.KRA
java -cp . comp.Comp ..\..\tests\OK-GER03.KRA
java -cp . comp.Comp ..\..\tests\OK-GER04.KRA
java -cp . comp.Comp ..\..\tests\OK-GER05.KRA
java -cp . comp.Comp ..\..\tests\OK-GER06.KRA
java -cp . comp.Comp ..\..\tests\OK-GER07.KRA
java -cp . comp.Comp ..\..\tests\OK-GER08.KRA
java -cp . comp.Comp ..\..\tests\OK-GER09.KRA
java -cp . comp.Comp ..\..\tests\OK-GER10.KRA
java -cp . comp.Comp ..\..\tests\OK-GER11.KRA
java -cp . comp.Comp ..\..\tests\OK-GER12.KRA
java -cp . comp.Comp ..\..\tests\OK-GER14.KRA
java -cp . comp.Comp ..\..\tests\OK-GER15.KRA
java -cp . comp.Comp ..\..\tests\OK-GER16.KRA
java -cp . comp.Comp ..\..\tests\OK-GER21.KRA
rem java -cp . comp.Comp ..\..\tests\OK-GER22.KRA

rem pause

rem move ..\..\t\ok-*.txt .
rem move ..\..\t\er-*.txt .
rem move ..\..\t\ok-*.exe .
rem move ..\..\tests\ok-*.txt .
rem move ..\..\tests\er-*.txt .
rem move ..\..\tests\ok-*.c .
rem move ..\..\tests\ok-*.exe .

set path=C:\MinGW\bin;%path%

del z.txt

g++ -o OK-GER01.exe OK-GER01.cpp
OK-GER01  < ..\..\t\30-enters.txt > OK-Out01.txt
g++ -o OK-GER02.exe OK-GER02.cpp
OK-GER02  < ..\..\t\30-enters.txt > OK-Out02.txt
g++ -o OK-GER03.exe OK-GER03.cpp
OK-GER03  < ..\..\t\30-enters.txt > OK-Out03.txt
g++ -o OK-GER04.exe OK-GER04.cpp
OK-GER04  < ..\..\t\30-enters.txt > OK-Out04.txt
g++ -o OK-GER06.exe OK-GER06.cpp
OK-GER06  < ..\..\t\30-enters.txt > OK-Out06.txt
g++ -o OK-GER07.exe OK-GER07.cpp
OK-GER07  < ..\..\t\30-enters.txt > OK-Out07.txt
g++ -o OK-GER08.exe OK-GER08.cpp
OK-GER08  < ..\..\t\30-enters.txt > OK-Out08.txt
g++ -o OK-GER09.exe OK-GER09.cpp
OK-GER09  < ..\..\t\30-enters.txt > OK-Out09.txt
g++ -o OK-GER10.exe OK-GER10.cpp
OK-GER10  < ..\..\t\30-enters.txt > OK-Out10.txt
g++ -o OK-GER12.exe OK-GER12.cpp
OK-GER12  < ..\..\t\30-enters.txt > OK-Out12.txt
g++ -o OK-GER13.exe OK-GER13.cpp
OK-GER13  < ..\..\t\30-enters.txt > OK-Out13.txt
g++ -o OK-GER14.exe OK-GER14.cpp
OK-GER14  < ..\..\t\30-enters.txt > OK-Out14.txt
g++ -o OK-GER15.exe OK-GER15.cpp
OK-GER15  < ..\..\t\30-enters.txt > OK-Out15.txt
g++ -o OK-GER16.exe OK-GER16.cpp
OK-GER16  < ..\..\t\30-enters.txt > OK-Out16.txt
g++ -o OK-GER21.exe OK-GER21.cpp
OK-GER21  < ..\..\t\30-enters.txt > OK-Out21.txt


copy OK-Out*.txt z.txt


g++ -o OK-GER05.exe OK-GER05.cpp
OK-GER05  < ..\..\t\sixnum.txt  > OK-Out05.txt

g++ -o OK-GER11.exe OK-GER11.cpp
OK-GER11  < ..\..\t\30-enters.txt > OK-Out11.txt


type OK-Out05.txt >> z.txt
type OK-Out11.txt >> z.txt


del *.obj
del *.bak


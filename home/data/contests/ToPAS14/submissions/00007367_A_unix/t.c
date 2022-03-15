#include <stdio.h>
#include <math.h>

main()
{

int a,m,d,ta,tb,tc,td,te,dj;
scanf("%d %d %d",&a,&m,&d);

if(m<3)
{
	a=a-1;
	m=m+12;
}
ta=a/100;
tb=ta/4;
if(a>1582){
	tc= 2 - 2 -ta + tb;
}
else if(m>10 && a==1582){
    tc= 2 - 2 -ta + tb;
}
else if(d>15 && m==10 && a==1582){
    tc= 2 - 2 -ta + tb;
}
if(a<=1582){
	tc=0;
}
else if(m<=10 && a==1582){
    tc=0;
}
else if(d<=4 && m==10 && a==1582){
    tc=0;
}
td=round(365.25*(a+4716));
te=round(30.6001*(m+1));
dj= td + te + d + tc - 1524;
printf("%d\n",dj);
return 0;
}

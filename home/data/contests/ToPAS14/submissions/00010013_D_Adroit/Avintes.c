
#include <stdio.h>
#include <string.h>
int main()
{
	char vec[24][30],med,med2,mes1,mes2;
	int d[24],i,p;
	float pp;
	
	for(i=0;i<24;i++)
	{
		scanf(" %s",vec[i]);
		scanf("%d",&d[i]);
	}
	mes1=d[0];
	mes2=d[12];
	med=(mes2+mes1)/2;
	med2=(d[23]+d[22]+d[21])/3;
	p=(med+med2)/2;
	pp=(med+med2)/2;
	if(pp>p)
	{
		p++;
	}
	printf("%d\n",p);
	return 0;
}




#include <stdlib.h>
#include <stdio.h>

int main()
{
	int d,a,b,e,c,dj,ano,mes,dia;
	scanf("%d %d %d",&ano,&mes,&dia);
	a=ano/100;
	b=a/4;
	if(ano>1582)
	{
		c=2-a+b;
	}
	else
		if(ano<1582)
		{
			c=0;
		}
		else
			if(ano==1582)
			{
				if(mes>10)
				{
					c=2-a+b;
				}
				else
					if(mes<10)
					{
						c=0;
					}
					else
					if(mes==10)
					{
						if(dia>15)
						{
							c=2-a+b;
						}
						else
							c=0;
					}
			}
	if(mes<3)
	{
		ano=ano-1;
		mes=mes+12;
	}
	d=365.25*(ano+4716);
	e=30.6001*(mes+1);
	dj=d+e+dia+c-1524;
	
	printf("%d\n",dj);
	return 0;
}





#include <stdio.h>

int main()
{
	int y,x,d,a,b,e,c,dj,z,dias;
	scanf("%d %d %d",&x,&y,&z);
	if(y<3)
	{
		x=x-1;
		y=y+12;
	}
	a=x/100;
	b=a/4;
	dias=x*365+(365/12)*y+4+z; 
	if(dias>1582*365+(365/12)*10+15)
	{
		c=2-a+b;
	}
	else
	{
		if(dias<=4+(10*(365/12))+(1582*365))
		{
			c=0;
		}
	}
	d=365.25*(x+4716);
	e=30.6001*(y+1);
	dj=d+e+z+c-1524;
	printf("%d",dj);
	return 0;
}





#include <stdio.h>

int main()
{
	int a,b,n,i,e,vec;
	scanf("%d",&n);
	e=0;
	a=0;
	for(i=0;i<n;i++)
	{
		b=a;
		a=vec;
		scanf("%d",&vec);
		if(i>=2 && a>b*2 && a>vec*2)
		{
		   e++;
	    }
    }
    printf("%d\n",e);
	return 0;
}


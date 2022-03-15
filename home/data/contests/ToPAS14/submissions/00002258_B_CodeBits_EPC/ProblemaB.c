#include<stdio.h>
int main()
{
	int n, array[1000],i, cont=0;
	scanf("%d",&n);
	for(i=0;i<n;i++)
	{
		scanf("%d",&array[i]);
	}
	
	for(i=1;i<n;i++)
	{
		if(array[i]>array[i+1]*2 && array[i]>array[i-1]*2)
		cont=cont+1;
	
	}
	
	printf("%d\n", cont);
return 0;
}

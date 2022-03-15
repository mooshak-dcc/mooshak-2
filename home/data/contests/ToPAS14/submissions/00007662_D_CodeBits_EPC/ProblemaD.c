#include<stdio.h>
int main()
{
int array[24],array2[24], i,m1,m2,m;

for(i=0;i<24;i++)
{

	scanf("%d %d",& array[i],& array2[i]);
	
	
}	
m1=(array2[23] + array2[22] + array2[21])/3;
m2=(array2[23-11]+array2[0])/2;
m=(m1+m2)/2;
printf("%d\n",m);

return 0;	
}

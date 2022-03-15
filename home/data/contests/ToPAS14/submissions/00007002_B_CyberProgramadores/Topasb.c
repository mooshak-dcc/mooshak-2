#include<stdio.h>
int main(){
int i,ns,n,cont,k;
cont=0;
scanf("%d\n",&ns);
if((ns>=3)&&(ns<=1000)){
    for(i=0;i<ns;i++){
        scanf("%d\n",&n);
        }
        k=n*2;
            if((k>i-1)&&(k>i+1)){
            cont++;
        }
    }
    else{
        printf("ERROR\n");
    }
    printf("%d\n",cont);
    return 0;
}

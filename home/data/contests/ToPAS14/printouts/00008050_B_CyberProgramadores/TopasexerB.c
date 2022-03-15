#include<stdio.h>
int main(){
int ns,d,i,conta=0;
scanf("%d",&ns);
if((ns>=3)&&(ns<=1000)){
    for(i=0;i<ns;i++){
        scanf("%d",&d);
    }
    if((d*2>i-1)&&(d*2>i+1)){
        conta=conta+1;
    }
    }else{
        printf("ERROR");
    }
    printf("%d",conta);
    return 0;
}

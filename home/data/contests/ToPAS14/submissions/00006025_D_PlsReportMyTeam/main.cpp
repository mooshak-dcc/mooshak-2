#include <iostream>
#include <cmath>
using namespace std;

int main()
{
   int meses[24];
   int stock[24];
   int media1,media2,media3;
   for(int i=0;i<24; i++){
   cin>>meses[i]>>stock[i];

   }
   media1=(stock[22]+stock[21]+stock[20])/3;
   media2=((stock[12]+stock[0])/2);
   media3=(media1+media2)/2;
   round(media3);
   cout<<media3<<endl;
   return 0;
}

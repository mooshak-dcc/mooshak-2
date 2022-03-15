#include <iostream>
#include <string>
#include <sstream>

using namespace std;

int main() {
	int quant;
	cin >> quant;
	while(quant < 3 || quant > 1000) {
		cin >> quant;
	}	

	int v[quant];

	int c = 0;
	for(int x=0;x<quant;x++)
	{
		cin >> v[x];
	}
	for(int x=0;x<quant;x++)
	{
		if((v[x-1]*2) < v[x] && (v[x+1]*2)< v[x])
		{
			c++;
		}
 	}	
	cout << c;
}

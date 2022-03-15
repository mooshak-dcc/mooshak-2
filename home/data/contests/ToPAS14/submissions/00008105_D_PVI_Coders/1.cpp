#include <iostream>
#include <vector>
#include <string>

using namespace std;

int main() {
    vector<int> valores;
    vector<int> datas;
    for (int i = 0; i< 24; i++) {
        int valor,data;
        cin >> data >> valor;
        valores.push_back(valor);
        datas.push_back(data);
    }
    int soma3meses = 0;
    for (int i = 0; i <= 2; i++) {
        soma3meses += valores[23-i];
    }
    int media3meses = soma3meses/3;
    int mediaanos = (valores[0] + valores[12])/2;
    int media = (media3meses + mediaanos)/2;
    cout << media << endl;
    return 0;
}

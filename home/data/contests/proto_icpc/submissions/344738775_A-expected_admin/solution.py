b = int(input().strip())
m = int(input().strip())

def derivative(b, m):
    return b * m ** (b - 1)

print(derivative(b, m)+'\n')

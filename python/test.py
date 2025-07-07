# python/test.py
import time
import sys

# Valeur par défaut
i = 0

# Si un argument est passé, on l'utilise comme valeur initiale
if len(sys.argv) > 1:
    try:
        i = int(sys.argv[1])
    except ValueError:
        print("❌ Le paramètre doit être un entier.", flush=True)
        sys.exit(1)

# Boucle infinie
while True:
    print(i, flush=True)
    i += 1
    time.sleep(1)

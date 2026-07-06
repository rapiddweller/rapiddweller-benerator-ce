#!/usr/bin/env bash
# Dual execution: run a descriptor through BOTH engines and assert the generated structure matches.
# Benerator runs it to out.csv; DATAMIMIC runs the converted descriptor and captures the rows. Compares
# the field-name set and the record count (values differ — independent RNGs — so structure only).
#   usage: datamimic_structural_compare.sh <descriptor.ben.xml> <generate-name> <converter-classpath>
set -uo pipefail
descriptor="$1"; gen="$2"; cp="$3"
work="$(mktemp -d)"
cp "$descriptor" "$work/d.ben.xml"

# 1) Benerator -> out.csv
( cd "$work" && java -cp "$cp" com.rapiddweller.benerator.main.Benerator d.ben.xml ) >/dev/null 2>&1 || true
if [ ! -f "$work/out.csv" ]; then
  echo "Benerator did not produce out.csv for $descriptor"; exit 1
fi
ben_fields="$(head -1 "$work/out.csv" | tr -d '\r' | tr ',' '\n' | sort | paste -sd, -)"
ben_rows="$(( $(grep -c . "$work/out.csv") - 1 ))"

# 2) convert, then 3) run in DATAMIMIC and capture the structure to a file (logs must not pollute it)
java -cp "$cp" com.rapiddweller.benerator.main.datamimic.DatamimicConverter "$work/d.ben.xml" "$work" >/dev/null 2>&1
python3 - "$work/d.datamimic.xml" "$gen" "$work/dm.txt" >/dev/null 2>&1 <<'PY'
import sys
from pathlib import Path
from datamimic_ce.data_mimic_test import DataMimicTest
descriptor, gen, out = sys.argv[1], sys.argv[2], sys.argv[3]
engine = DataMimicTest(test_dir=Path(descriptor).parent, filename=Path(descriptor).name, capture_test_result=True)
engine.test_with_timer()
rows = engine.capture_result()[gen]
Path(out).write_text(",".join(sorted(rows[0].keys())) + " " + str(len(rows)))
PY
if [ ! -s "$work/dm.txt" ]; then
  echo "DATAMIMIC did not run the converted $descriptor"; exit 1
fi
read -r dm_fields dm_rows < "$work/dm.txt"

# 4) compare structure
echo "Benerator : fields=[$ben_fields] rows=$ben_rows"
echo "DATAMIMIC : fields=[$dm_fields] rows=$dm_rows"
if [ "$ben_fields" = "$dm_fields" ] && [ "$ben_rows" = "$dm_rows" ]; then
  echo "structural match: same fields and record count in both engines"
else
  echo "STRUCTURAL MISMATCH between Benerator and DATAMIMIC output"; exit 1
fi

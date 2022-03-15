#!/bin/bash

# EXIT CODES
# 0 - Accepted
# 1 - Presentation Error
# 2 - Wrong Answer
# 3 - Evaluation Skipped
# 4 - Output Limit Exceeded
# 5 - Memory Limit Exceeded
# 6 - Time Limit Exceeded
# 7 - Invalid Function
# 8 - Invalid Exit Value
# 9 - Runtime Error
# 10 - Compile Time Error
# 11 - Invalid Submission
# 12 - Program Size Exceeded
# 13 - Requires Reevaluation
# 14 - Evaluating

cat $1 | grep -q "Math.sqrt"

[[ $? -eq 0 ]] && exit 0 || exit 2

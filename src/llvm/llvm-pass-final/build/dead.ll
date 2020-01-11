; ModuleID = 'dead.c'
source_filename = "dead.c"
target datalayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-unknown-linux-gnu"

; Function Attrs: noinline nounwind uwtable
define dso_local i32 @foo() #0 {
entry:
  %a = alloca i32, align 4
  %b = alloca i32, align 4
  %c = alloca i32, align 4
  %d = alloca i32, align 4
  store i32 7, i32* %a, align 4
  %0 = load i32, i32* %a, align 4
  %mul = mul nsw i32 %0, 2
  store i32 %mul, i32* %b, align 4
  %1 = load i32, i32* %b, align 4
  %2 = load i32, i32* %a, align 4
  %sub = sub nsw i32 %1, %2
  store i32 %sub, i32* %c, align 4
  %3 = load i32, i32* %c, align 4
  %4 = load i32, i32* %a, align 4
  %div = sdiv i32 %3, %4
  store i32 %div, i32* %d, align 4
  %5 = load i32, i32* %b, align 4
  ret i32 %5
}

attributes #0 = { noinline nounwind uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "min-legal-vector-width"="0" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{!"clang version 9.0.1 (/afs/inf.ed.ac.uk/user/s16/s1615906/ug3-ct/llvm-project/clang c1a0a213378a458fbea1a5c77b315c7dce08fd05)"}

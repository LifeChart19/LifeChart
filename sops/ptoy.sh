
echo "# Encoder-250630.0"
echo ""

awk '
NR == 1 && /^# Decoder-/ {
  prev = 1;
  next
}

NR == 2 && prev {
  prev = 0;
  next
}

/^[[:space:]]*#/ {
  print $0       # 주석 줄 그대로 출력
  next
}

/^[[:space:]]*$/ {
  print "#"
  next
}

{
  # escape 된 = 처리도 필요. 일단 key에는 없다는 전제로 진행. 차후 필요할 수 있음.

  if(multiline){
    temp = $0
    sub(/^[[:space:]]+/, "", temp) # 앞 공백 삭제
    sub(/[[:space:]]+$/, "", temp) # 뒤 공백 삭제
  } else {
    match($0, /[^\\]=/)
    key = substr($0, 1, RSTART)
    temp = substr($0, (RSTART+RLENGTH))
    sub(/^[[:space:]]+/, "", temp) # 앞 공백 삭제
    sub(/[[:space:]]+$/, "", temp) # 뒤 공백 삭제
  }

  # \가 있는지, Escape 된 \ 인지 확인. (해당 부분 함수로 분리?)
  if(temp ~ /\\$/) {
    cnt = 0
    for(i=length(temp); i>0; i--) {
      if (substr(temp,i,1) == "\\") {
        cnt++
      } else {
        break
      }
    }

    if (cnt % 2 == 1) {
      sub(/\\$/, "", temp)
      multiline=1
      val = val temp
      next # \ 줄바꿈이 있다면 아직 처리하지 않고 다음 줄로.
    }
  }
  # \ 줄바꿈이 없다면 마저 처리

  val = val temp

  if (val ~ /,/) {
    print key ":"
    n = split(val, arr, /[[:space:]]*,[[:space:]]*/)
    for (i=1; i<=n; i++) {
      print "  - " arr[i]
    }
  }
  else {
    print key ": " val
  }

  multiline=0
  key=""
  val=""


# sh 매개변수 (대상 파일)
}' "$1"
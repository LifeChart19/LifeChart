
echo "# Decoder-250630.0"
echo ""

awk '
NR == 1 {
  next # 첫 줄 버전 체크
}

/^[[:space:]]*$/ {
  if(multiline){
    print "    " temp
    multiline=0
  }
  print ""
  next
}

/^#[[:space:]]*$/ {
  if(multiline){
    print "    " temp
    multiline=0
  }
  print ""
  next
}

/^#.*$/ {
  if(multiline){
    print "    " temp
    multiline=0
  }
  print $0
  next
}

{
  # 멀티라인 체크
  # key 끝에 \ 부착, 들여쓰기 4칸, 마지막 줄은 \ X
  # yaml array 탐지 lockahead. temp는 이전 줄을 출력.
  if(multiline){
    # multiline 첫 진입 시 print 생략

    if(temp){
      if($0 ~ /^ /) {
        print "    " temp ",\\"
      } else {
        print "    " temp
        multiline=0
      }
    }
    temp = $0
    sub(/^[[:space:]]+- /, "", temp) # 앞 공백, - 삭제

  }

  if(!multiline) {
    match($0, /[^\\]:/)
    key = substr($0, 1, RSTART)
    temp = substr($0, (RSTART+RLENGTH))

    # value 단 맨 앞 검사. 공백 확인 (없으면 multiline 돌입)
    if(!temp && !multiline) {
      multiline=1
      print key "=\\"
      next # multiline 이라면 첫 줄에 value 없음.
    }

    sub(/^[[:space:]]+/, "", temp) # value 앞 공백 삭제

  }

  # multiline에 이미 진입했거나, 해당 key가 multiline이 아니거나

  if(multiline){

  } else {
    print key "=" temp
  }

# sh 매개변수 (대상 파일)
}

END {
    if(multiline){
      multiline=0
      print "    " temp
    }
}

' "$1"
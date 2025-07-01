#!/bin/bash

# 프로젝트 초기 설정을 위한 shell script 입니다.
# Windows는 git bash 등을 사용하여 실행해주세요.

# --------------------------
# 설정
# --------------------------

GIT_MESSAGE_DIR=".gitmessage.txt"

SOPS_DIR="./sops"
SOPS_BIN="$SOPS_DIR/sops"

SOPS_VER="v3.10.2"

PROJECT_NAME="LifeChart"

echo "$PROJECT_NAME 초기 설정을 진행합니다."


template_ok=false
sops_ok=false
gpg_ok=false
gpg_import_ok=false

# --------------------------
# gitmessage 설정 확인
# --------------------------

echo ""
echo "-------------"

template=$(git config --get commit.template)

echo "(local) 현재 commit.template 설정 값 : $template"

if [[ "$template" != "$GIT_MESSAGE_DIR" ]]; then
  echo "local commit.template를 .gitmessage.txt로 변경합니까?"

  read -r -p "[y or n] : " temp

  if [[ "$temp" == "y" ]]; then
    echo "설정합니다."
    git config commit.template .gitmessage.txt
    template_ok=true
  elif [[ "$temp" == "n" ]]; then
    echo "설정하지 않습니다."
  else
    echo "입력이 올바르지 않습니다. 설정을 생략합니다."
  fi

else
  template_ok=true
fi

# --------------------------
# gitmessage 설정 확인
# --------------------------

echo ""
echo "-------------"

if [ -x "$SOPS_BIN" ]; then
  echo "경로에 SOPS가 설치되어 있습니다. ($(./sops/sops -v | grep -E '^sops '))"
  sops_ok=true
else
  echo "경로에 SOPS가 설치되어있지 않습니다. ($SOPS_BIN)"
  echo "sops 설치를 진행합니까?"
  read -r -p "[y or n] : " temp

  if [[ "$temp" = "y" ]]; then
    echo "설치 정보를 확인합니다."
    case "$(uname -s)" in
      Linux*)
        OS="linux"
        ;;
      Darwin*)
        OS="darwin"
        ;;
      MINGW* | MSYS* | CYGWIN* | Windows_NT)
        OS="windows"
        ;;
      *)
        OS="unknown"
        ;;
    esac

    case "$(uname -m)" in
      x86_64* | amd64*)
        ARCH="amd64"
        ;;
      arm64 | aarch64*)
        ARCH="aarch64"
        ;;
      armv7l* | armv6l*)
        ARCH="aarch32"
        ;;
      *)
        ARCH="unknown"
        ;;
    esac
    echo "운영체제 : $OS"
    echo "아키텍쳐 : $ARCH"
    echo "버전 : $SOPS_VER"
    echo "위 정보가 맞습니까?"
    read -r -p "[y or n] : " temp

    if [[ "$temp" == "n" || "$OS" == "unknown" || "$ARCH" == "unknown" ]]; then
      echo "수동으로 입력합니다."
      until [[ "$temp" == "windows" || "$temp" == "darwin" || "$temp" == "linux" ]]; do
        read -r -p "[windows or darwin or linux] : " OS
        temp="$OS"
      done

      until [[ "$temp" == "amd64" || "$temp" == "aarch64" || "$temp" == "aarch32" ]]; do
        read -r -p "[amd64 or aarch64 or aarch32] : " ARCH
        temp="$ARCH"
      done
    fi

    if [[ "$ARCH" = "aarch32" ]]; then
      echo "SOPS는 32bit release를 제공하지 않습니다."
    else
      case "$OS" in
        linux)
          if [[ "$ARCH" == "amd64" ]]; then
            filename="sops-${SOPS_VER}-1.x86_64.rpm"
          elif [[ "$ARCH" == "aarch64" ]]; then
            filename="sops-${SOPS_VER}-1.aarch64.rpm"
          else
            filename="x"
          fi
          ;;
        windows)
          if [[ "$ARCH" == "amd64" ]]; then
            filename="sops-${SOPS_VER}.amd64.exe"
          elif [[ "$ARCH" == "aarch64" ]]; then
            filename="sops-${SOPS_VER}.arm64.exe"
          else
            filename="x"
          fi
          ;;
        darwin)
          filename="sops-v${VERSION}.darwin"
          ;;
      esac

      if [[ "$filename" = "x" ]]; then
        echo "적합한 파일을 찾지 못했습니다. 수동으로 설치해주세요. ($SOPS_BIN)"
      else
        URL="https://github.com/getsops/sops/releases/download/v3.10.2/$filename"
        echo "설치 URL : $URL"
        echo "설치 경로 : $SOPS_BIN"
        curl -fL "$URL" -o "$SOPS_BIN"
        if [[ $? -eq 0 ]]; then
          echo "SOPS 설치를 마쳤습니다. ($(./sops/sops -v | grep -E '^sops '))"
          sops_ok=true
        else
          echo "SOPS 설치에 실패했습니다."
        fi
      fi
    fi

  elif [[ "$temp" = "n" ]]; then
    echo "설치를 생략합니다."
  else
    echo "입력이 올바르지 않습니다. 설치를 생략합니다."
  fi

fi

# --------------------------
# GnuPG (GPG) 설치 확인
# --------------------------

echo ""
echo "-------------"


if command -v gpg; then
  echo "GPG가 설치되어 있습니다. : $(gpg --version | head -n 1)"
  gpg_ok=true
else
  echo "GPG가 설치되지 않았습니다."
  echo "운영체제 : $OS"

  if [[ $OS == "windows" ]]; then
    echo "windows는 bash 명령어를 통한 설치가 어렵습니다. 별도로 설치해주세요."
  else
    case "$OS" in
      linux)
        gpg_install="sudo apt install -y gnupg"
        ;;
      darwin)
        gpg_install="brew install gnupg"
        ;;
    esac

    echo "설치를 시작합니까? 다음 명령이 실행됩니다."
    echo "> $gpg_install "
    read -r -p "[y or n] : " temp

    if [[ "$temp" = "y" ]]; then
      echo "설치를 시작합니다."
      eval "$gpg_install"

      if [ $? -eq 0 ]; then
        echo "설치가 성공했습니다."
        gpg_ok=true
      else
        echo "설치가 실패했습니다."
      fi
    else
      echo "설치를 생략합니다."
    fi
  fi
fi


# --------------------------
# GPG import *_pub.asc
# --------------------------

if [[ "$gpg_ok" == true ]]; then

  echo ""
  echo "-------------"

  set -e  # 오류 발생 시 즉시 종료

  echo "Importing GPG public keys..."
  for key in ./sops/public_keys/*.asc; do
    echo ""
    gpg --import "$key" 2>/dev/null && echo "  Imported: $key"
  done

  gpg_import_ok=true

fi

# --------------------------
# 보고서 작성
# --------------------------

echo ""
echo ""

echo "-------------"
echo "-------------"

echo "gitmessage = $template_ok"
echo "SOPS = $sops_ok"
echo "GPG = $gpg_ok"
echo "GPG *_pub.asc import = $gpg_import_ok"


# --------------------------
# 추가 설정
# --------------------------

echo "-------------"
echo "-------------"
echo ""
echo ""

if [[ "$gpg_ok" == true ]]; then

  read -r -p "[exit : 0 / gpg_export : 1] : " temp

  until [[ "$temp" == "0" || "$temp" == "1" ]]; do
    read -r -p "[exit : 0 / gpg_export : 1] : " temp
  done

  if [[ "$temp" == "0" ]]; then
    exit 0
  fi
else
  read -r -p "종료하려면 엔터를 입력하세요." temp
  exit 0
fi

# --------------------------
# GPG export *_pub.asc
# --------------------------

if [[ "$gpg_ok" == true ]]; then



echo ""
echo "-------------"

set -e  # 오류 발생 시 즉시 종료

SOPS_YAML="./sops/.sops.yaml"
PUBLIC_KEYS_DIR="./sops/public_keys"
mkdir -p "$PUBLIC_KEYS_DIR"

# ▶ 1. .sops.yaml 없으면 초기 템플릿 생성
if [ ! -f "$SOPS_YAML" ]; then
  echo "creation_rules:" > "$SOPS_YAML"
fi

# ▶ 2. 비밀키가 있는 내 키 리스트 (keygrip 기준)
mapfile -t MY_KEYS < <(gpg --list-secret-keys --with-colons | awk -F: '/^uid:/ {print $10}')

# ▶ 3. 키가 없는 경우 종료
if [ ${#MY_KEYS[@]} -eq 0 ]; then
  echo "[!] 비밀키가 포함된 GPG 키가 없습니다."
  exit 1
fi

# ▶ 4. 메뉴 반복
while true; do
  echo
  echo "[ 내 비밀키 보유 GPG 키 목록 ]"
  for i in "${!MY_KEYS[@]}"; do
    echo "$((i+1)). ${MY_KEYS[$i]}"
  done
  echo "0. 종료"

  read -rp "추가할 키 번호 선택: " num

  if [[ "$num" == "0" ]]; then
    echo "[*] 종료합니다."
    break
  elif [[ "$num" =~ ^[0-9]+$ ]] && (( num >= 1 && num <= ${#MY_KEYS[@]} )); then
    email="${MY_KEYS[$((num-1))]}"
    keyfile="$PUBLIC_KEYS_DIR/$(echo "$email" | sed 's/[^a-zA-Z0-9._-]/_/g')_pub.asc"

    echo "[*] $email 공개키 추출 및 파일 저장 중..."
    gpg --armor --export "$email" > "$keyfile"

    # ▶ 키 fingerprint 추출
    fingerprint=$(gpg --with-colons --fingerprint "$email" | awk -F: '/^fpr:/ {print $10; exit}')

    if [ -z "$fingerprint" ]; then
      echo "[!] fingerprint 추출 실패: $email"
      continue
    fi

    # ▶ 기존 pgp 배열에 fingerprint가 있는지 확인
    if grep -q "$fingerprint" "$SOPS_YAML"; then
      echo "[!] 이미 등록된 fingerprint입니다: $fingerprint"
      continue
    fi

    echo "[*] .sops.yaml에 fingerprint 추가 중..."

    # ▶ pgp 배열 마지막 요소 앞에 줄바꿈과 쉼표 포함하여 추가
    # ▶ pgp 배열 마지막 요소 앞에 fingerprint 추가 (줄바꿈 후 쉼표는 앞에)
    awk -v fpr="$fingerprint" '
      BEGIN { in_pgp = 0 }
      /^\s*pgp:\s*\[/ { in_pgp = 1; print; next }
      in_pgp && /^\s*"/ {
        print "      \"" fpr "\",";
        in_pgp = 0
      }
      in_pgp && /\]/ {
        print "      \"" fpr "\"";
        in_pgp = 0
      }
      { print }
    ' "$SOPS_YAML" > "$SOPS_YAML.tmp" && mv "$SOPS_YAML.tmp" "$SOPS_YAML"

    echo "[+] 완료: $email ($fingerprint)"
  else
    echo "[!] 유효하지 않은 입력입니다."
  fi
done

fi


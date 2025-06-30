
# --------------------------
#
# --------------------------

GIT_MESSAGE_DIR=".gitmessage.txt"

SOPS_DIR="./sops"
SOPS_BIN="$SOPS_DIR/sops"

SOPS_VER="v3.10.2"

PROJECT_NAME="LifeChart"

echo "$PROJECT_NAME 초기 설정을 진행합니다."

# --------------------------
# gitmessage 설정 확인
# --------------------------

template=$(git config --get commit.template)

echo "현재 local commit.template 설정 값 : $template"

if [[ "$template" != "$GIT_MESSAGE_DIR" ]]; then
  echo "local commit.template를 .gitmessage.txt로 변경합니까?"

  read -r -p "[y or n] : " temp

  if [[ "$temp" == "y" ]]; then
    echo "설정합니다."
    git config commit.template .gitmessage.txt
  elif [[ "$temp" == "n" ]]; then
    echo "설정하지 않습니다."
  else
    echo "입력이 올바르지 않습니다. 설정을 생략합니다."
  fi

else
  echo "넘어갑니다."
fi

# --------------------------
# gitmessage 설정 확인
# --------------------------



if [ -x "$SOPS_BIN" ]; then
  echo "경로에 SOPS가 설치되어 있습니다. ($(./sops/sops -v | grep -E '^sops '))"

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
      done

      until [[ "$temp" == "amd64" || "$temp" == "aarch64" || "$temp" == "aarch32" ]]; do
        read -r -p "[amd64 or aarch64 or aarch32] : " ARCH
      done
    fi

    if [[ "$ARCH" = "aarch32" ]]; then
      echo "SOPS는 32bit release를 제공하지 않습니다."
    else
      case "$OS" in
        linux)
          if [[ "$ARCH" == "amd64" ]]; then
            filename="sops-${SOPS_VER}-1.x86_64.rpm"
          elif [[ "$ARCH" == "aarch64" ]] then
            filename="sops-${SOPS_VER}-1.aarch64.rpm"
          else
            filename="x"
          fi
          ;;
        windows)
          if [[ "$ARCH" == "amd64" ]]; then
            filename="sops-${SOPS_VER}.amd64.exe"
          elif [[ "$ARCH" == "aarch64" ]] then
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
        curl -L "$URL" -o "$SOPS_BIN"

        echo "SOPS 설치를 마쳤습니다. ($(./sops/sops -v | grep -E '^sops '))"
      fi
    fi

  elif [[ "$temp" = "n" ]] then
    echo "설치를 생략합니다."
  else
    echo "입력이 올바르지 않습니다. 설치를 생략합니다."
  fi

fi

read -r -p "마치려면 엔터를 입력하세요." waiting


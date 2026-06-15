export sites = "gopkg.in/yaml.v3:gopkg.in/yaml.v3:force \
           golang.org/x/net:golang.org/x/net:force \
           golang.org/x/sys:golang.org/x/sys:force \
           golang.org/x/arch:golang.org/x/arch:force \
           golang.org/x/text:golang.org/x/text:force \
           golang.org/x/crypto:golang.org/x/crypto:force \
           github.com/gin-gonic/gin:github.com/gin-gonic/gin:force \
           github.com/tidwall/gjson:github.com/tidwall/gjson:force \
           github.com/tidwall/sjson:github.com/tidwall/sjson:force \
           github.com/goccy/go-json:github.com/goccy/go-json:force \
           github.com/tidwall/match:github.com/tidwall/match:force \
           github.com/cloudwego/iasm:github.com/cloudwego/iasm:force \
           github.com/leodido/go-urn:github.com/leodido/go-urn:force \
           github.com/tidwall/pretty:github.com/tidwall/pretty:force \
           github.com/bytedance/sonic:github.com/bytedance/sonic:force \
           github.com/davecgh/go-spew:github.com/davecgh/go-spew:force \
           github.com/gin-contrib/sse:github.com/gin-contrib/sse:force \
           github.com/mattn/go-isatty:github.com/mattn/go-isatty:force \
           google.golang.org/protobuf:google.golang.org/protobuf:force \
           github.com/stretchr/testify:github.com/stretchr/testify:force \
           github.com/json-iterator/go:github.com/json-iterator/go:force \
           github.com/billziss-gh/golib:github.com/billziss-gh/golib:force \
           github.com/fsnotify/fsnotify:github.com/fsnotify/fsnotify:force \
           github.com/cloudwego/base64x:github.com/cloudwego/base64x:force \
           github.com/klauspost/cpuid/v2:github.com/klauspost/cpuid/v2:force \
           github.com/modern-go/reflect2:github.com/modern-go/reflect2:force \
           github.com/pmezard/go-difflib:github.com/pmezard/go-difflib:force \
           github.com/ugorji/go/codec:github.com/ugorji/go/codec/codec:force \
           github.com/modern-go/concurrent:github.com/modern-go/concurrent:force \
           github.com/pelletier/go-toml/v2:github.com/pelletier/go-toml/v2:force \
           github.com/go-playground/locales:github.com/go-playground/locales:force \
           github.com/gabriel-vasile/mimetype:github.com/gabriel-vasile/mimetype:force \
           github.com/bytedance/sonic/loader:github.com/bytedance/sonic/loader/loader:force \
           github.com/twitchyliquid64/golang-asm:github.com/twitchyliquid64/golang-asm:force \
           github.com/go-playground/validator/v10:github.com/go-playground/validator/v10:force \
           github.com/go-playground/universal-translator:github.com/go-playground/universal-translator:force"

do_compile:prepend() {
    cd ${S}/src/import
    for s in $sites; do
        site_dest=$(echo $s | cut -d: -f1)
        site_source=$(echo $s | cut -d: -f2)
        force_flag=$(echo $s | cut -d: -f3)

        mkdir -p vendor.copy/$site_dest

        # create a temporary exclude file
        exclude_file=$(mktemp)

        find vendor.fetch/$site_source -type d -print0 |         xargs -0 du -sBM 2>/dev/null |         awk '{if ($1+0 > 500) print substr($0, index($0,$2))}' |         sed 's|^vendor.fetch/||' > "$exclude_file"

        if [ -n "$force_flag" ]; then
            echo "[INFO] $site_dest: force copying .go files"
            rm -rf vendor.copy/$site_dest
            rsync -a                 --exclude='vendor/'                 --exclude='.git/'                 --exclude-from="$exclude_file"                 vendor.fetch/$site_source/ vendor.copy/$site_dest
        else
            if [ -n "$(ls -A vendor.copy/$site_dest/*.go 2> /dev/null)" ]; then
                echo "[INFO] vendor.fetch/$site_source -> $site_dest: go copy skipped (files present)"
                true
            else
                echo "[INFO] $site_dest: copying .go files"
                rsync -a                     --exclude='vendor/'                     --exclude='.git/'                     --exclude-from="$exclude_file"                     vendor.fetch/$site_source/ vendor.copy/$site_dest
            fi
        fi

        rm -f "$exclude_file"
    done
}

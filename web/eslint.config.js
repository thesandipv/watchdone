import antfu from "@antfu/eslint-config"

export default antfu(
  {
    ignores: [".agents/**"],
    svelte: true,
    typescript: true,
    stylistic: {
      indent: 2,
      quotes: "double",
      semi: false,
    },
    rules: {
      "style/indent-binary-ops": "off",
      "style/brace-style": ["error", "1tbs", { allowSingleLine: true }],
    },
  },
  {
    files: ["src/lib/components/ui/**/*.svelte"],
    rules: {
      "svelte/no-navigation-without-resolve": "off",
    },
  },
)

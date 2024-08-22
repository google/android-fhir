# Troubleshooting

## Common Build Problems

1. _Build failed due to Java heap space memory:_

    Set `org.gradle.jvmargs=-Xmx2048m` in a `gradle.properties`.

    If it still fails, you can further increase the memory.

2. _More than one file was found with OS independent path `mozilla/public-suffix-list.txt`:_

    Add this line to the packagingOptions in the build.gradle of you app:

    `packagingOptions {
            exclude 'mozilla/public-suffix-list.txt'
        }`

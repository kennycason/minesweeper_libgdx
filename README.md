Mine Sweeper
=============

A Mine Sweeper implementation using LibGDX.

## Controls

| ------------------ | ------------------------ |
| Single Click       | Uncover tile             |
| Left + Right Click | Flag tile as safe        |
| R                  | Reset Game               |
| H                  | Help (Uncover safe tile) |
| Escape             | Exit Game                |


## Screenshot

<img src="/screenshots/minesweeper01.png" width="100%"/><br/>
<img src="/screenshots/minesweeper02.png" width="100%"/><br/>


## Run Desktop Application

```bash
gradle clean dist
java -jar desktop/build/libs/desktop-1.0.jar
```

## Run Html Application

```bash
gradle clean dist
# run local server to serve generated html app
cd html/build/dist
python -m SimpleHTTPServer
```

### Super dev mode for debugging/testing

Follow steps: [here](https://github.com/libgdx/libgdx/wiki/Gradle-on-the-Commandline#packaging-for-the-web)



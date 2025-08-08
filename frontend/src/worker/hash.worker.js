import SparkMD5 from "spark-md5"

self.onmessage = async function (e) {

    const { file } = e.data;
    const chunkSize = 2 * 1024 * 1024; // 2MB
    const chunks = Math.ceil(file.size / chunkSize); // 获取文件块数
    const spark = new SparkMD5.ArrayBuffer();

    for (let i = 0; i < chunks; i++) {
        const start = i * chunkSize;
        const end = Math.min(file.size, start + chunkSize);
        const slice = file.slice(start, end);
        const buffer = await slice.arrayBuffer();
        spark.append(buffer);
        postMessage( { progress: Math.floor((i + 1) / chunks * 100)})
    }

    const hash = spark.end();
    postMessage({ hash, done: true });

}

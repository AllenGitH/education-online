package com.atguigu.qzpoint.producer

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.{SparkConf, SparkContext}

object CourseLearnProducer {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("courseProducer").setMaster("local[*]")
    val ssc = new SparkContext(sparkConf)
    System.setProperty("hadoop.home.dir", "D:\\hadoop\\hadoop-common-2.2.0-bin-master")
    val resultLog = ssc.textFile(this.getClass.getResource("/course_learn.log").toURI.getPath, 10)
      .foreachPartition(partitoin => {
        val props = new Properties()
        props.put("bootstrap.servers", "hadoop001:9092,hadoop002:9092,hadoop003:9092")
        props.put("acks", "1")
        props.put("batch.size", "16384")
        props.put("linger.ms", "10")
        props.put("buffer.memory", "33554432")
        props.put("key.serializer",
          "org.apache.kafka.common.serialization.StringSerializer")
        props.put("value.serializer",
          "org.apache.kafka.common.serialization.StringSerializer")
        val producer = new KafkaProducer[String, String](props)
        partitoin.foreach(item => {
          val msg = new ProducerRecord[String, String]("course_learn", item)
          producer.send(msg)
        })
        producer.flush()
        producer.close()
      })
  }
}

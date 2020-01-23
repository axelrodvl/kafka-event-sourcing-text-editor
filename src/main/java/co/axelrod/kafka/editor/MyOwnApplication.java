//package co.axelrod.kafka.producer;
//
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.streams.KeyValue;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.StreamsConfig;
//import org.apache.kafka.streams.kstream.KStream;
//
//import java.util.Properties;
//
//public class MyOwnApplication {
//    public static void main(String[] args) {
//        final Properties streamsConfiguration = new Properties();
//        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-music-charts");
//        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        // Serdes (Serialize/Deserialize)
//        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String());
//        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long());
//
//        final StreamsBuilder streamsBuilder = new StreamsBuilder();
//        final KStream<String, Long> playEvents = streamsBuilder.stream("play-events");
//        final KStream<String, Long> playsBySong = playEvents.filter((id, count) -> id.contains("abc"))
//                .map(((key, value) -> KeyValue.pair(key.toString(), value)));
//
//        // KStream - stream of records
//        // KTable - changelog with only the latest value for a given key
//
//        // Join - you can enrich records by merging data from different sources
//        //final KStream<String, Long> songPlays = playsBySong.leftJoin(son)
//    }

//        StreamsBuilder builder = new StreamsBuilder();
//        KStream<String, String> textLines = builder.stream("TextLinesTopic");
//        KTable<String, Long> wordCounts = textLines
//                .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))
//                .groupBy((key, word) -> word)
//                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"));
//        wordCounts.toStream().to("WordsWithCountsTopic", Produced.with(Serdes.String(), Serdes.Long()));
//
//        KafkaStreams streams = new KafkaStreams(builder.build(), props);
//        streams.start();

//}

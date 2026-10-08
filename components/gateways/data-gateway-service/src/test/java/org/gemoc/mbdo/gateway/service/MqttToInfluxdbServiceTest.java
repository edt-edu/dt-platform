package org.gemoc.mbdo.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.gemoc.mbdo.gateway.dto.GatewayServiceConfiguration;
import org.gemoc.mbdo.gateway.dto.GatewayServiceConfiguration.InfluxDBConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.MockedStatic;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import com.influxdb.client.write.Point;

/** Messages are queued on the client's batching write API, never written one by one with a blocking call. */
class MqttToInfluxdbServiceTest {

	private static final InfluxDBConfiguration CONFIGURATION =
			new InfluxDBConfiguration("http://localhost:8086", "token", "mbdo", "factory", null, null, null);

	private final InfluxDBClient client = mock(InfluxDBClient.class);
	private final WriteApi writeApi = mock(WriteApi.class);
	private MockedStatic<InfluxDBClientFactory> clientFactory;
	private MqttToInfluxdbService service;

	@BeforeEach
	void setUp() {
		// the service creates its client from the gateway configuration: hand it the mock client instead
		clientFactory = mockStatic(InfluxDBClientFactory.class);
		clientFactory.when(() -> InfluxDBClientFactory.create(anyString(), any(char[].class), anyString(), anyString()))
				.thenReturn(client);
		when(client.makeWriteApi(any(WriteOptions.class))).thenReturn(writeApi);

		GatewayService gatewayService = mock(GatewayService.class);
		when(gatewayService.getGatewayServiceConfiguration()).thenReturn(
				new GatewayServiceConfiguration("gateway", null, null, null, CONFIGURATION, null, null, null));
		service = new MqttToInfluxdbService(gatewayService);
	}

	@AfterEach
	void tearDown() {
		clientFactory.close();
	}

	@Test
	void aMessageIsQueuedOnTheBatchingWriteApi() {
		service.processAndStoreMessage("Physical_Twin_VGR1", "PLC/PLC01/VacuumGripper/VacuumGripper01/armEncoder",
				"{\"value\":\"1500\",\"timestamp\":\"2026-10-08T13:40:00.123Z\"}", true, true);

		ArgumentCaptor<Point> point = ArgumentCaptor.forClass(Point.class);
		verify(writeApi).writePoint(point.capture());
		verify(client, never()).getWriteApiBlocking();
		String line = point.getValue().toLineProtocol();
		assertTrue(line.startsWith("Physical_Twin_VGR1,topic=PLC/PLC01/VacuumGripper/VacuumGripper01/armEncoder "), line);
		assertTrue(line.contains("integerValue=1500i"), line);
		assertTrue(line.contains("timestamp=\"2026-10-08T13:40:00.123Z\""), line);
	}

	@Test
	void eachPointKeepsTheTimeItsMessageWasReceived() {
		Point point = MqttToInfluxdbService.toPoint("m", "t", "{\"value\":\"true\"}", 1_760_000_000_123L);

		assertTrue(point.toLineProtocol().endsWith(" 1760000000123"), point.toLineProtocol());
		assertTrue(point.toLineProtocol().contains("booleanValue=true"), point.toLineProtocol());
	}

	@Test
	void aNonJsonMessageIsNotRecorded() {
		assertNull(MqttToInfluxdbService.toPoint("m", "t", "not json {", 0));

		service.processAndStoreMessage("m", "t", "not json {", false, false);
		verify(writeApi, never()).writePoint(any(Point.class));
	}

	@Test
	void theQueuedPointsAreWrittenBeforeTheConnectionIsClosed() {
		service.close();

		InOrder order = inOrder(writeApi, client);
		order.verify(writeApi).close();
		order.verify(client).close();
	}

	@Test
	void batchingDefaultsApplyWhenNotConfigured() {
		WriteOptions defaults = MqttToInfluxdbService.writeOptions(CONFIGURATION);
		assertEquals(MqttToInfluxdbService.DEFAULT_BATCH_SIZE, defaults.getBatchSize());
		assertEquals(MqttToInfluxdbService.DEFAULT_FLUSH_INTERVAL_MS, defaults.getFlushInterval());
		assertEquals(MqttToInfluxdbService.DEFAULT_BUFFER_LIMIT, defaults.getBufferLimit());

		WriteOptions configured = MqttToInfluxdbService.writeOptions(
				new InfluxDBConfiguration("http://localhost:8086", "token", "mbdo", "factory", 500, 200, 20_000));
		assertEquals(500, configured.getBatchSize());
		assertEquals(200, configured.getFlushInterval());
		assertEquals(20_000, configured.getBufferLimit());
	}
}

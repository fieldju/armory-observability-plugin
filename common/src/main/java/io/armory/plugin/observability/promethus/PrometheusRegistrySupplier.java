/*
 * Copyright 2020 Armory, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.armory.plugin.observability.promethus;

import io.armory.plugin.observability.model.PluginConfig;
import io.armory.plugin.observability.model.PluginMetricsPrometheusConfig;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import java.util.function.Supplier;

/**
 * Supplier bean so that we don't create an actual bean of the prometheus registry. We do this so
 * that our composite registry is used and we don't confuse Spectator/Micrometer.
 */
public class PrometheusRegistrySupplier implements Supplier<MeterRegistry> {

  private final PluginMetricsPrometheusConfig prometheusConfig;
  private final CollectorRegistry collectorRegistry;
  private final Clock clock;

  public PrometheusRegistrySupplier(
      PluginConfig pluginConfig, CollectorRegistry collectorRegistry, Clock clock) {

    prometheusConfig = pluginConfig.getMetrics().getPrometheus();
    this.collectorRegistry = collectorRegistry;
    this.clock = clock;
  }

  @Override
  public PrometheusMeterRegistry get() {
    if (!prometheusConfig.isEnabled()) {
      return null;
    }
    var config = new PrometheusRegistryConfig(prometheusConfig);
    return new PrometheusMeterRegistry(config, collectorRegistry, clock);
  }
}

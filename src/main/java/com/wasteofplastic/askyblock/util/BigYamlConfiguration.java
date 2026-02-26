package com.wasteofplastic.askyblock.util;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

import java.lang.reflect.Method;
import java.util.Map;

public class BigYamlConfiguration extends YamlConfiguration {

    private static final boolean USE_YAML_LIMIT;
    private static final boolean USE_OPTIONS_LIMIT;

    static {
        boolean useYamlLimit = false;
        for (Method method : LoaderOptions.class.getDeclaredMethods()) {
            if (method.getName().equals("setCodePointLimit")) {
                useYamlLimit = true;
                break;
            }
        }
        USE_YAML_LIMIT = useYamlLimit;

        boolean useOptionsLimit = false;
        for (Method method : YamlConfigurationOptions.class.getDeclaredMethods()) {
            if (method.getName().equals("codePointLimit")) {
                useOptionsLimit = true;
                break;
            }
        }
        USE_OPTIONS_LIMIT = useOptionsLimit;
    }

    private final DumperOptions yamlOptions = new DumperOptions();
    private final Representer yamlRepresenter = new YamlRepresenter();
    private final LoaderOptions loaderOptions = new LoaderOptions();
    private final Yaml yaml = new Yaml(new YamlConstructor(), yamlRepresenter, yamlOptions, loaderOptions);

    public BigYamlConfiguration() {
        super();
        if (USE_OPTIONS_LIMIT) {
            options().codePointLimit(Integer.MAX_VALUE);
        }
    }

    @Override
    public String saveToString() {
        if (USE_OPTIONS_LIMIT) {
            return super.saveToString();
        }

        yamlOptions.setIndent(options().indent());
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        String header = buildHeader();
        String dump = yaml.dump(getValues(false));

        if (dump.equals(BLANK_CONFIG)) {
            dump = "";
        }

        return header + dump;
    }

    @Override
    public void loadFromString(String contents) throws InvalidConfigurationException {
        if (USE_OPTIONS_LIMIT) {
            super.loadFromString(contents);
            return;
        }

        Validate.notNull(contents, "Contents cannot be null");

        if (USE_YAML_LIMIT) {
            loaderOptions.setCodePointLimit(500 * 1024 * 1024); // 500MB
        }

        Map<?, ?> input;
        try {
            input = yaml.load(contents);
        } catch (YAMLException e) {
            throw new InvalidConfigurationException(e);
        } catch (ClassCastException e) {
            throw new InvalidConfigurationException("Top level is not a Map.");
        }

        String header = parseHeader(contents);
        if (header.length() > 0) {
            options().header(header);
        }

        if (input != null) {
            convertMapsToSections(input, this);
        }
    }
}

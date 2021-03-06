package azkaban.jobtype.connectors;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import azkaban.jobtype.javautils.FileUtils;
import azkaban.jobtype.javautils.ValidationUtils;

public class TdchParameters {
  private final static Logger _logger = Logger.getLogger(TdchParameters.class);
  private final static String TERADATA_JDBC_URL_PREFIX = "jdbc:teradata://";
  private final static String TERADATA_JDBC_URL_CHARSET_KEY = "/CHARSET=";
  private final static String DEFAULT_CHARSET = "UTF8";
  private static final String DEFAULT_RETRIEVE_METHOD = "split.by.amp";

  private final String _mrParams;
  private final String _libJars;
  private final String _tdJdbcClassName;
  private final String _tdUrl;
  private final String _fileFormat;
  private final Optional<String> _fieldSeparator;
  private final String _jobType;
  private final String _userName;
  private final String _credentialName;
  private final Optional<String> _avroSchemaPath;
  private final Optional<String> _avroSchemaInline;
  private final String _numMappers;

  private final TdchType _tdchType;

  //From HDFS to Teradata
  private final String _sourceHdfsPath;
  private final String _targetTdTableName;
  private final Optional<String> _tdInsertMethod;

  //From Teradata to HDFS
  private final Optional<String> _sourceQuery;
  private final Optional<String> _sourceTdTableName;
  private final Optional<String> _tdRetrieveMethod;
  private final String _targetHdfsPath;

  public static Builder builder() {
    return new Builder();
  }

  private TdchParameters(Builder builder) {
    this._mrParams = builder._mrParams;
    this._libJars = builder._libJars;
    this._tdJdbcClassName = builder._tdJdbcClassName;
    this._tdUrl = builder._tdUrl;
    this._fileFormat = builder._fileFormat;
    this._fieldSeparator = Optional.fromNullable(builder._fieldSeparator);

    this._jobType = builder._jobType;
    this._userName = builder._userName;
    this._credentialName = builder._credentialName;

    this._avroSchemaPath = Optional.fromNullable(builder._avroSchemaPath);
    this._avroSchemaInline = Optional.fromNullable(builder._avroSchemaInline);

    this._numMappers = Integer.toString(builder._numMappers);
    this._tdchType = builder._tdchType;

    this._sourceHdfsPath = builder._sourceHdfsPath;
    this._targetTdTableName = builder._targetTdTableName;
    this._tdInsertMethod = Optional.fromNullable(builder._tdInsertMethod);  //Default by TDCH is batch.insert

    this._sourceQuery = Optional.fromNullable(builder._sourceQuery);
    this._sourceTdTableName = Optional.fromNullable(builder._sourceTdTableName);
    this._targetHdfsPath = builder._targetHdfsPath;
    this._tdRetrieveMethod = Optional.fromNullable(builder._tdRetrieveMethod);
  }

  private enum TdchType {
    HDFS_TO_TERADATA,
    TERADATA_TO_HDFS
  }

  public static class Builder {
    private String _mrParams;
    private String _libJars;
    private String _tdJdbcClassName;
    private String _tdHostName;
    private String _tdCharSet;
    private String _tdUrl;
    private String _fileFormat;
    private String _fieldSeparator;
    private String _jobType;
    private String _userName;
    private String _credentialName;
    private String _avroSchemaPath;
    private String _avroSchemaInline;
    private int _numMappers;

    private TdchType _tdchType;

    private String _sourceHdfsPath;
    private String _targetTdTableName;
    private String _tdInsertMethod;

    private String _sourceQuery;
    private String _sourceTdTableName;
    private String _targetHdfsPath;
    private String _tdRetrieveMethod;

    public Builder mrParams(String mrParams) {
      this._mrParams = mrParams;
      return this;
    }

    public Builder libJars(String libJars) {
      Collection<String> filePaths = FileUtils.listFiles(libJars, TdchConstants.LIB_JAR_DELIMITER);
      this._libJars = Joiner.on(TdchConstants.LIB_JAR_DELIMITER).skipNulls().join(filePaths);
      return this;
    }

    public Builder tdJdbcClassName(String tdJdbcClassName) {
      this._tdJdbcClassName = tdJdbcClassName;
      return this;
    }

    public Builder teradataHostname(String hostname) {
      this._tdHostName = hostname;
      return this;
    }

    public Builder teradataCharset(String charSet) {
      this._tdCharSet = charSet;
      return this;
    }

    public Builder fileFormat(String fileFormat) {
      this._fileFormat = fileFormat;
      return this;
    }

    public Builder fieldSeparator(String fieldSeparator) {
      this._fieldSeparator = fieldSeparator;
      return this;
    }

    public Builder jobType(String jobType) {
      this._jobType = jobType;
      return this;
    }

    public Builder tdInsertMethod(String tdInsertMethod) {
      this._tdInsertMethod = tdInsertMethod;
      return this;
    }

    public Builder userName(String userName) {
      this._userName = userName;
      return this;
    }

    public Builder credentialName(String credentialName) {
      this._credentialName = credentialName;
      return this;
    }

    public Builder avroSchemaPath(String avroSchemaPath) {
      this._avroSchemaPath = avroSchemaPath;
      return this;
    }

    public Builder avroSchemaInline(String avroSchemaInline) {
      this._avroSchemaInline = avroSchemaInline;
      return this;
    }

    public Builder numMapper(int numMappers) {
      this._numMappers = numMappers;
      return this;
    }

    public Builder sourceHdfsPath(String sourceHdfsPath) {
      this._sourceHdfsPath = sourceHdfsPath;
      return this;
    }

    public Builder targetTdTableName(String targetTdTableName) {
      this._targetTdTableName = targetTdTableName;
      return this;
    }

    public Builder sourceQuery(String sourceQuery) {
      this._sourceQuery = sourceQuery;
      return this;
    }

    public Builder sourceTdTableName(String sourceTdTableName) {
      this._sourceTdTableName = sourceTdTableName;
      return this;
    }

    public Builder targetHdfsPath(String targetHdfsPath) {
      this._targetHdfsPath = targetHdfsPath;
      return this;
    }

    public Builder tdRetrieveMethod(String tdRetrieveMethod) {
      this._tdRetrieveMethod = tdRetrieveMethod;
      return this;
    }

    public TdchParameters build() {
      validate();
      return new TdchParameters(this);
    }

    private void validate() {
      ValidationUtils.validateNotEmpty(_tdJdbcClassName, "tdJdbcClassName");
      ValidationUtils.validateNotEmpty(_jobType, "jobType");
      ValidationUtils.validateNotEmpty(_userName, "userName");
      ValidationUtils.validateNotEmpty(_credentialName, "credentialName");
      ValidationUtils.validateNotEmpty(_tdHostName, "teradata host name");

      if(StringUtils.isEmpty(_fileFormat)) {
        _fileFormat = TdchConstants.AVRO_FILE_FORMAT;
      } else {
        ValidationUtils.validateNotEmpty(_fileFormat, "fileFormat");
      }

      if(TdchConstants.AVRO_FILE_FORMAT.equals(_fileFormat)) {
        //Validate the existence of avro schema, but confirm only one of them exist.
        if(StringUtils.isEmpty(_avroSchemaPath) && StringUtils.isEmpty(_avroSchemaInline)) {
          throw new IllegalArgumentException("Either " + TdchConstants.AVRO_SCHEMA_PATH_KEY + " or "
                                             + TdchConstants.AVRO_SCHEMA_INLINE_KEY + " should be provided");
        }

        if(!StringUtils.isEmpty(_avroSchemaPath) && !StringUtils.isEmpty(_avroSchemaInline)) {
          throw new IllegalArgumentException("Only one of " + TdchConstants.AVRO_SCHEMA_PATH_KEY + " and "
                                             + TdchConstants.AVRO_SCHEMA_INLINE_KEY + " should be provided");
        }
      }

      if(_numMappers <= 0) {
        throw new IllegalArgumentException("Number of mappers needs to be defined and has to be greater than 0.");
      }

      String charSet = StringUtils.isEmpty(_tdCharSet) ? DEFAULT_CHARSET : _tdCharSet;
      _tdUrl = TERADATA_JDBC_URL_PREFIX + _tdHostName + TERADATA_JDBC_URL_CHARSET_KEY + charSet;

      boolean isHdfsToTd = !StringUtils.isEmpty(_sourceHdfsPath) && !StringUtils.isEmpty(_targetTdTableName);
      boolean isTdToHdfs = !(StringUtils.isEmpty(_sourceTdTableName) && StringUtils.isEmpty(_sourceQuery))
                           && !StringUtils.isEmpty(_targetHdfsPath);

      if(!(isHdfsToTd || isTdToHdfs)) {
        throw new IllegalArgumentException("Source and target are not defined. " + this);
      }

      if(isHdfsToTd && isTdToHdfs) {
        throw new IllegalArgumentException("Cannot choose multiple source and multiple target. " + this);
      }

      if(isHdfsToTd) {
        ValidationUtils.validateNotEmpty(_sourceHdfsPath, "tdInsertMethod");
        ValidationUtils.validateNotEmpty(_targetTdTableName, "tdInsertMethod");
      }

      if(isTdToHdfs) {
        if(!StringUtils.isEmpty(_sourceTdTableName) && !StringUtils.isEmpty(_sourceQuery)) {
          throw new IllegalArgumentException("Cannot choose multiple source");
        }
      }

      if(isHdfsToTd) {
        _tdchType = TdchType.HDFS_TO_TERADATA;
      } else {
        _tdchType = TdchType.TERADATA_TO_HDFS;
      }
    }
  }

  public String[] toTdchParams() {
    ImmutableList.Builder<String> listBuilder = ImmutableList.builder();
    if(!StringUtils.isEmpty(_mrParams)) {
      listBuilder.add(_mrParams);
    }
    if(!StringUtils.isEmpty(_libJars)) {
      listBuilder.add("-libjars").add(_libJars);
    }

    listBuilder.add("-url")
               .add(_tdUrl)
               .add("-classname")
               .add(_tdJdbcClassName)
               .add("-fileformat")
               .add(_fileFormat)
               .add("-jobtype")
               .add(_jobType)
               .add("-username")
               .add(_userName)
               .add("-password")
               .add(_credentialName)
               .add("-nummappers")
               .add(_numMappers);

    if(_avroSchemaPath.isPresent()) {
      listBuilder.add("-avroschemafile")
                 .add(_avroSchemaPath.get());
    }

    if(_avroSchemaInline.isPresent()) {
      listBuilder.add("-avroschema")
                 .add(_avroSchemaInline.get());
    }

    if(_fieldSeparator.isPresent()) {
      listBuilder.add("-separator")
                 .add(_fieldSeparator.get());
    }


    if(TdchType.HDFS_TO_TERADATA.equals(_tdchType)) {
      listBuilder.add("-sourcepaths")
                 .add(_sourceHdfsPath)
                 .add("-targettable")
                 .add(_targetTdTableName);

      if(_tdInsertMethod.isPresent()) {
        listBuilder.add("-method")
                   .add(_tdInsertMethod.get());
      }
    } else if (TdchType.TERADATA_TO_HDFS.equals(_tdchType)){
      listBuilder.add("-targetpaths")
                 .add(_targetHdfsPath);

      if(_sourceTdTableName.isPresent()) {
        listBuilder.add("-sourcetable")
                   .add(_sourceTdTableName.get());

        if (_tdRetrieveMethod.isPresent()) {
          listBuilder.add("-method")
                     .add(_tdRetrieveMethod.get());
        } else {
          listBuilder.add("-method")
                     .add(DEFAULT_RETRIEVE_METHOD);
        }

      } else if (_sourceQuery.isPresent()) {
        listBuilder.add("-sourcequery")
                   .add(_sourceQuery.get());
      } else {
        throw new IllegalArgumentException("No source defined."); //This should not happen as it shouldn't have been instantiated by builder.
      }
    } else {
      throw new UnsupportedOperationException("Unsupported TDCH type: " + _tdchType);
    }

    List<String> paramList = listBuilder.build();
    String[] params = paramList.toArray(new String[paramList.size()]);
    return params;
  }

  private String getMaskedPassword() {
    StringBuilder maskedPassword = new StringBuilder(_credentialName.length());
    for (int i = 0; i < _credentialName.length(); i++) {
      maskedPassword.append("*");
    }
    return maskedPassword.toString();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("TdchParameters [_mrParams=").append(_mrParams)
            .append(", _libJars=").append(_libJars)
            .append(", _tdJdbcClassName=").append(_tdJdbcClassName)
            .append(", _tdUrl=").append(_tdUrl)
            .append(", _fileFormat=").append(_fileFormat)
            .append(", _fieldSeparator=").append(_fieldSeparator)
            .append(", _jobType=").append(_jobType)
            .append(", _userName=").append(_userName)
            .append(", _credentialName=").append(getMaskedPassword())
            .append(", _avroSchemaPath=").append(_avroSchemaPath)
            .append(", _avroSchemaInline=").append(_avroSchemaInline)
            .append(", _numMappers=").append(_numMappers)
            .append(", _tdchType=").append(_tdchType)
            .append(", _sourceHdfsPath=").append(_sourceHdfsPath)
            .append(", _targetTdTableName=").append(_targetTdTableName)
            .append(", _tdInsertMethod=").append(_tdInsertMethod)
            .append(", _sourceQuery=").append(_sourceQuery)
            .append(", _sourceTdTableName=").append(_sourceTdTableName)
            .append(", _tdRetrieveMethod=").append(_tdRetrieveMethod)
            .append(", _targetHdfsPath=").append(_targetHdfsPath)
            .append("]");
    return builder.toString();
  }
}

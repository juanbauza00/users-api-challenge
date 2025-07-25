/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package org.example.avro;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class ClientData extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -473503336632435976L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"ClientData\",\"namespace\":\"org.example.avro\",\"fields\":[{\"name\":\"nombre\",\"type\":\"string\",\"doc\":\"Nombre del cliente\"},{\"name\":\"apellido\",\"type\":\"string\",\"doc\":\"Apellido del cliente\"},{\"name\":\"fechaNacimiento\",\"type\":\"string\",\"doc\":\"Fecha de nacimiento en formato ISO (YYYY-MM-DD)\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<ClientData> ENCODER =
      new BinaryMessageEncoder<ClientData>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<ClientData> DECODER =
      new BinaryMessageDecoder<ClientData>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<ClientData> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<ClientData> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<ClientData> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<ClientData>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this ClientData to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a ClientData from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a ClientData instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static ClientData fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  /** Nombre del cliente */
  private java.lang.CharSequence nombre;
  /** Apellido del cliente */
  private java.lang.CharSequence apellido;
  /** Fecha de nacimiento en formato ISO (YYYY-MM-DD) */
  private java.lang.CharSequence fechaNacimiento;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public ClientData() {}

  /**
   * All-args constructor.
   * @param nombre Nombre del cliente
   * @param apellido Apellido del cliente
   * @param fechaNacimiento Fecha de nacimiento en formato ISO (YYYY-MM-DD)
   */
  public ClientData(java.lang.CharSequence nombre, java.lang.CharSequence apellido, java.lang.CharSequence fechaNacimiento) {
    this.nombre = nombre;
    this.apellido = apellido;
    this.fechaNacimiento = fechaNacimiento;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return nombre;
    case 1: return apellido;
    case 2: return fechaNacimiento;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: nombre = (java.lang.CharSequence)value$; break;
    case 1: apellido = (java.lang.CharSequence)value$; break;
    case 2: fechaNacimiento = (java.lang.CharSequence)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'nombre' field.
   * @return Nombre del cliente
   */
  public java.lang.CharSequence getNombre() {
    return nombre;
  }


  /**
   * Sets the value of the 'nombre' field.
   * Nombre del cliente
   * @param value the value to set.
   */
  public void setNombre(java.lang.CharSequence value) {
    this.nombre = value;
  }

  /**
   * Gets the value of the 'apellido' field.
   * @return Apellido del cliente
   */
  public java.lang.CharSequence getApellido() {
    return apellido;
  }


  /**
   * Sets the value of the 'apellido' field.
   * Apellido del cliente
   * @param value the value to set.
   */
  public void setApellido(java.lang.CharSequence value) {
    this.apellido = value;
  }

  /**
   * Gets the value of the 'fechaNacimiento' field.
   * @return Fecha de nacimiento en formato ISO (YYYY-MM-DD)
   */
  public java.lang.CharSequence getFechaNacimiento() {
    return fechaNacimiento;
  }


  /**
   * Sets the value of the 'fechaNacimiento' field.
   * Fecha de nacimiento en formato ISO (YYYY-MM-DD)
   * @param value the value to set.
   */
  public void setFechaNacimiento(java.lang.CharSequence value) {
    this.fechaNacimiento = value;
  }

  /**
   * Creates a new ClientData RecordBuilder.
   * @return A new ClientData RecordBuilder
   */
  public static org.example.avro.ClientData.Builder newBuilder() {
    return new org.example.avro.ClientData.Builder();
  }

  /**
   * Creates a new ClientData RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new ClientData RecordBuilder
   */
  public static org.example.avro.ClientData.Builder newBuilder(org.example.avro.ClientData.Builder other) {
    if (other == null) {
      return new org.example.avro.ClientData.Builder();
    } else {
      return new org.example.avro.ClientData.Builder(other);
    }
  }

  /**
   * Creates a new ClientData RecordBuilder by copying an existing ClientData instance.
   * @param other The existing instance to copy.
   * @return A new ClientData RecordBuilder
   */
  public static org.example.avro.ClientData.Builder newBuilder(org.example.avro.ClientData other) {
    if (other == null) {
      return new org.example.avro.ClientData.Builder();
    } else {
      return new org.example.avro.ClientData.Builder(other);
    }
  }

  /**
   * RecordBuilder for ClientData instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<ClientData>
    implements org.apache.avro.data.RecordBuilder<ClientData> {

    /** Nombre del cliente */
    private java.lang.CharSequence nombre;
    /** Apellido del cliente */
    private java.lang.CharSequence apellido;
    /** Fecha de nacimiento en formato ISO (YYYY-MM-DD) */
    private java.lang.CharSequence fechaNacimiento;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(org.example.avro.ClientData.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.nombre)) {
        this.nombre = data().deepCopy(fields()[0].schema(), other.nombre);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.apellido)) {
        this.apellido = data().deepCopy(fields()[1].schema(), other.apellido);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.fechaNacimiento)) {
        this.fechaNacimiento = data().deepCopy(fields()[2].schema(), other.fechaNacimiento);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing ClientData instance
     * @param other The existing instance to copy.
     */
    private Builder(org.example.avro.ClientData other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.nombre)) {
        this.nombre = data().deepCopy(fields()[0].schema(), other.nombre);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.apellido)) {
        this.apellido = data().deepCopy(fields()[1].schema(), other.apellido);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.fechaNacimiento)) {
        this.fechaNacimiento = data().deepCopy(fields()[2].schema(), other.fechaNacimiento);
        fieldSetFlags()[2] = true;
      }
    }

    /**
      * Gets the value of the 'nombre' field.
      * Nombre del cliente
      * @return The value.
      */
    public java.lang.CharSequence getNombre() {
      return nombre;
    }


    /**
      * Sets the value of the 'nombre' field.
      * Nombre del cliente
      * @param value The value of 'nombre'.
      * @return This builder.
      */
    public org.example.avro.ClientData.Builder setNombre(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.nombre = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'nombre' field has been set.
      * Nombre del cliente
      * @return True if the 'nombre' field has been set, false otherwise.
      */
    public boolean hasNombre() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'nombre' field.
      * Nombre del cliente
      * @return This builder.
      */
    public org.example.avro.ClientData.Builder clearNombre() {
      nombre = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'apellido' field.
      * Apellido del cliente
      * @return The value.
      */
    public java.lang.CharSequence getApellido() {
      return apellido;
    }


    /**
      * Sets the value of the 'apellido' field.
      * Apellido del cliente
      * @param value The value of 'apellido'.
      * @return This builder.
      */
    public org.example.avro.ClientData.Builder setApellido(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.apellido = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'apellido' field has been set.
      * Apellido del cliente
      * @return True if the 'apellido' field has been set, false otherwise.
      */
    public boolean hasApellido() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'apellido' field.
      * Apellido del cliente
      * @return This builder.
      */
    public org.example.avro.ClientData.Builder clearApellido() {
      apellido = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'fechaNacimiento' field.
      * Fecha de nacimiento en formato ISO (YYYY-MM-DD)
      * @return The value.
      */
    public java.lang.CharSequence getFechaNacimiento() {
      return fechaNacimiento;
    }


    /**
      * Sets the value of the 'fechaNacimiento' field.
      * Fecha de nacimiento en formato ISO (YYYY-MM-DD)
      * @param value The value of 'fechaNacimiento'.
      * @return This builder.
      */
    public org.example.avro.ClientData.Builder setFechaNacimiento(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.fechaNacimiento = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'fechaNacimiento' field has been set.
      * Fecha de nacimiento en formato ISO (YYYY-MM-DD)
      * @return True if the 'fechaNacimiento' field has been set, false otherwise.
      */
    public boolean hasFechaNacimiento() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'fechaNacimiento' field.
      * Fecha de nacimiento en formato ISO (YYYY-MM-DD)
      * @return This builder.
      */
    public org.example.avro.ClientData.Builder clearFechaNacimiento() {
      fechaNacimiento = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ClientData build() {
      try {
        ClientData record = new ClientData();
        record.nombre = fieldSetFlags()[0] ? this.nombre : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.apellido = fieldSetFlags()[1] ? this.apellido : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.fechaNacimiento = fieldSetFlags()[2] ? this.fechaNacimiento : (java.lang.CharSequence) defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<ClientData>
    WRITER$ = (org.apache.avro.io.DatumWriter<ClientData>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<ClientData>
    READER$ = (org.apache.avro.io.DatumReader<ClientData>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.nombre);

    out.writeString(this.apellido);

    out.writeString(this.fechaNacimiento);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.nombre = in.readString(this.nombre instanceof Utf8 ? (Utf8)this.nombre : null);

      this.apellido = in.readString(this.apellido instanceof Utf8 ? (Utf8)this.apellido : null);

      this.fechaNacimiento = in.readString(this.fechaNacimiento instanceof Utf8 ? (Utf8)this.fechaNacimiento : null);

    } else {
      for (int i = 0; i < 3; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.nombre = in.readString(this.nombre instanceof Utf8 ? (Utf8)this.nombre : null);
          break;

        case 1:
          this.apellido = in.readString(this.apellido instanceof Utf8 ? (Utf8)this.apellido : null);
          break;

        case 2:
          this.fechaNacimiento = in.readString(this.fechaNacimiento instanceof Utf8 ? (Utf8)this.fechaNacimiento : null);
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}











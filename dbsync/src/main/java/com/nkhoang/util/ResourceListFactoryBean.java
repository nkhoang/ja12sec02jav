package com.nkhoang.util;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a List of values from the specified resources.
 */
public class ResourceListFactoryBean extends ListFactoryBean {

   /**
    * The List implementation class to create
    */
   private Class targetListClass = null;

   /**
    * The resources to create the list from
    */
   private Resource[] locations = null;

   /**
    * Flag for whether the source list is set
    */
   private boolean sourceListSet = false;

   @Override
   /**
    * Set the source List, just used to detect whether
    * a sourceList is set
    * @param sourceList the source list to create the list from.
    */
   public void setSourceList(List sourceList) {
      super.setSourceList(sourceList);
      sourceListSet = true;
   }

   @Override
   /**
    * To allow the creation of the list class
    * @param targetListClass the class of the list to instantiate
    */
   public void setTargetListClass(Class targetListClass) {
      super.setTargetListClass(targetListClass);
      this.targetListClass = targetListClass;
   }

   /**
    * Set a location of the list file to load.
    *
    * @param location the resource to load the settings f
    */
   public void setLocation(Resource location) {
      this.locations = new Resource[]{location};
   }

   /**
    * Set locations of list files to be loaded.
    *
    * @param locations the locations of the resources
    */
   public void setLocations(Resource[] locations) {
      this.locations = locations;
   }

   @Override
   @SuppressWarnings("unchecked")
   /**
    * Create a list from the resources, delegating the
    * super class if any source list has been set.
    * @return the list instantiated from the context definition
    */
   protected List createInstance() {
      List result = null;
      if (this.sourceListSet) {
         result = super.createInstance();
      }
      if (this.locations != null) {
         List resourceList = null;
         if (result == null) {
            result = instantiateListClass();
         }

         Class valueType = null;

         for (Resource resource : locations) {
            List contents = this.resolveList(resource);

            if (this.targetListClass != null) {
               valueType = GenericCollectionTypeResolver.getCollectionType(this.targetListClass);
            }
            if (valueType != null) {
               TypeConverter converter = getBeanTypeConverter();
               for (Object elem : contents) {
                  result.add(converter.convertIfNecessary(elem, valueType));
               }
            } else {
               result.addAll(contents);
            }
         }
      }
      if (result == null) {
         throw new IllegalArgumentException("You must set either the sourceList, locations or location parameters");
      }
      return result;
   }

   /**
    * Instantiates the list class
    *
    * @return the list class as defined by the context
    */
   private List instantiateListClass() {
      List result;
      if (this.targetListClass != null) {
         result = (List) BeanUtils.instantiateClass(this.targetListClass);
      } else {
         result = new ArrayList();
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   /**
    * Loads the resource into a list of strings
    * @return the list of strings loaded from the resource
    */
   private List<String> resolveList(Resource resource) {
      InputStream inStream = null;
      List<String> lines = null;
      try {
         inStream = resource.getInputStream();
         lines = IOUtils.readLines(inStream);
      } catch (IOException e) {
         throw new IllegalArgumentException("Could not read resource " + resource.getDescription(), e);
      } finally {
         IOUtils.closeQuietly(inStream);
      }
      return lines;
   }

}